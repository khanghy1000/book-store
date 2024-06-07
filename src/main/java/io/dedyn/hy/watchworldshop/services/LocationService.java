package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.location.District;
import io.dedyn.hy.watchworldshop.entities.location.Province;
import io.dedyn.hy.watchworldshop.entities.location.Ward;
import io.dedyn.hy.watchworldshop.repositories.DistrictRepository;
import io.dedyn.hy.watchworldshop.repositories.ProvinceRepository;
import io.dedyn.hy.watchworldshop.repositories.WardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @Autowired
    public LocationService(ProvinceRepository provinceRepository, DistrictRepository districtRepository, WardRepository wardRepository) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    public List<Province> findAllProvince() {
        return provinceRepository.findAll(Sort.by("fullName").ascending());
    }

    public List<District> findAllDistrictByProvinceCode(String provinceCode) {
        return districtRepository.findAllByProvinceCode(provinceCode, Sort.by("fullName").ascending());
    }

    public List<Ward> findAllWardByDistrictCode(String districtCode) {
        return wardRepository.findAllByDistrictCode(districtCode, Sort.by("fullName").ascending());
    }

    public Ward findWardByCode(String wardCode) {
        return wardRepository.findById(wardCode).orElse(null);
    }
}
