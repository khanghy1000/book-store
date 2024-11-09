package io.dedyn.hy.bookstore.controller.management;

import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.entities.book.BookImage;
import io.dedyn.hy.bookstore.services.BookService;
import io.dedyn.hy.bookstore.services.CategoryService;
import io.dedyn.hy.bookstore.services.PublisherService;
import io.dedyn.hy.bookstore.utils.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/management/books")
public class BookManagementController {
    private final BookService bookService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    @Autowired
    public BookManagementController(BookService bookService, PublisherService publisherService, CategoryService categoryService) {
        this.bookService = bookService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;
    }


    @RequestMapping("")
    public String index(@RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "") String keyword,
                        Model model) {
        Page<Book> books = bookService.findByKeyword(keyword, page);

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        return "management/books/index";
    }

    @RequestMapping("/create")
    public String create(Model model) {
        Book book = Book.builder().discountPercent(0.0).shippingFee(30000.0).enabled(false).build();
        model.addAttribute("book", book);
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "management/books/book_form";
    }

    @PostMapping("/create")
    public String create(@Valid Book book,
                         BindingResult bindingResult,
                         @RequestParam("main-image") MultipartFile mainImage,
                         @RequestParam("images") MultipartFile[] images,
                         @RequestParam(name = "specIds", required = false) Long[] specIds,
                         @RequestParam(name = "specNames", required = false) String[] specNames,
                         @RequestParam(name = "specValues", required = false) String[] specValues,
                         @RequestParam(name = "authorIds", required = false) Long[] authorIds,
                         @RequestParam(name = "authorNames", required = false) String[] authorNames,
                         Model model,
                         RedirectAttributes redirectAttributes) throws IOException {
        book.setId(null);
        boolean isUniqueName = bookService.isUniqueName(book);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.book", "Tên sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("publishers", publisherService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "management/books/book_form";
        }

        setNewImages(book, mainImage, images);
        setSpecs(book, specIds, specNames, specValues);
        setAuthors(book, authorIds, authorNames);
        book = bookService.save(book);
        saveImages(book, mainImage, images);

        redirectAttributes.addFlashAttribute("message", "Thêm sách mới \"" + book.getName() + "\" thành công");
        return "redirect:/management/books";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "management/books/book_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Book book,
                       BindingResult bindingResult,
                       @RequestParam("main-image") MultipartFile mainImage,
                       @RequestParam("images") MultipartFile[] images,
                       @RequestParam(name = "savedImageIds", required = false) Long[] savedImageIds,
                       @RequestParam(name = "savedImageFileNames", required = false) String[] savedImageFileNames,
                       @RequestParam(name = "specIds", required = false) Long[] specIds,
                       @RequestParam(name = "specNames", required = false) String[] specNames,
                       @RequestParam(name = "specValues", required = false) String[] specValues,
                       @RequestParam(name = "authorIds", required = false) Long[] authorIds,
                       @RequestParam(name = "authorNames", required = false) String[] authorNames,
                       Model model,
                       RedirectAttributes redirectAttributes) throws IOException {
        boolean isUniqueName = bookService.isUniqueName(book);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.book", "Tên sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("publishers", publisherService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "management/books/book_form";
        }

        setExistingImages(book, savedImageIds, savedImageFileNames);
        setNewImages(book, mainImage, images);
        setSpecs(book, specIds, specNames, specValues);
        setAuthors(book, authorIds, authorNames);
        book = bookService.save(book);
        saveImages(book, mainImage, images);
        deleteOrphanImages(book);

        redirectAttributes.addFlashAttribute("message", "Cập nhật sách " + book.getId() + " thành công");
        return "redirect:/management/books";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws IOException {
        bookService.deleteById(id);
        FileUploadUtil.deleteDirectory("file_upload/books/" + id + "/images");
        FileUploadUtil.deleteDirectory("file_upload/books/" + id);
        redirectAttributes.addFlashAttribute("message", "Xóa sách thành công");
        return "redirect:/management/books";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "books";
    }

    private void setExistingImages(Book book, Long[] savedImageIds, String[] savedImageFileNames) {
        if (savedImageIds == null || savedImageIds.length == 0) return;

        Set<BookImage> images = new LinkedHashSet<>();

        for (int i = 0; i < savedImageIds.length; i++) {
            BookImage image = BookImage.builder().id(savedImageIds[i]).fileName(savedImageFileNames[i]).book(book).build();
            images.add(image);
        }

        book.setBookImages(images);
    }

    private void setNewImages(Book book, MultipartFile mainImage, MultipartFile[] images) {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImage.getOriginalFilename()));
            book.setMainImage(fileName);
        } else {
            if (book.getMainImage().isEmpty()) book.setMainImage(null);
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            if (!book.containsImage(fileName)) {
                book.addImage(fileName);
            }
        }
    }

    private void saveImages(Book book, MultipartFile mainImage, MultipartFile[] images) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImage.getOriginalFilename()));
            String uploadDir = "file_upload/books/" + book.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (images.length > 0) {
            String uploadDir = "file_upload/books/" + book.getId() + "/images";
            for (MultipartFile image : images) {
                if (image.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, image);
            }
        }
    }

    private void deleteOrphanImages(Book book) throws IOException {
        String dir = "file_upload/books/" + book.getId() + "/images";
        Path path = Paths.get(dir);

        try {
            Files.list(path).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!book.containsImage(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.out.println("Could not delete file: " + fileName);
                    }
                }
            });

        } catch (IOException e) {
            System.out.println("Could not list directory: " + dir);
        }
    }

    private void setSpecs(Book book, Long[] specIds, String[] specNames, String[] specValues) {
        if (specNames == null || specValues == null) return;

        for (int i = 0; i < specNames.length; i++) {
            if (specNames[i].isEmpty() && specValues[i].isEmpty()) continue;
            if (specIds[i] != null && specIds[i] != 0) {
                book.addSpec(specIds[i], specNames[i], specValues[i]);
            } else {
                book.addSpec(null, specNames[i], specValues[i]);
            }
        }
    }


    private void setAuthors(Book book, Long[] authorIds, String[] authorNames) {
        if (authorNames == null) return;

        for (int i = 0; i < authorNames.length; i++) {
            if (authorNames[i].isEmpty()) continue;
            if (authorIds[i] != null && authorIds[i] != 0) {
                book.addAuthor(authorIds[i], authorNames[i]);
            } else {
                book.addAuthor(null, authorNames[i]);
            }
        }
    }
}
