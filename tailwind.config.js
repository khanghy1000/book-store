const defaultTheme = require('tailwindcss/defaultTheme')

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/main/resources/templates/**/*.html",
    ],
    theme: {
        extend: {
            fontFamily: {
                sans: ["Inter", ...defaultTheme.fontFamily.sans],
            },
            gridTemplateColumns: {
                'auto-fill': 'repeat(auto-fill, minmax(200px, 1fr))',
            },
        },
    },

    darkMode: 'selector',
    plugins: [
        require("daisyui"),
        require('@tailwindcss/typography'),
    ],
}

