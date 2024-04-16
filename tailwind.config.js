/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/main/resources/templates/**/*.{html,js}",
        'node_modules/preline/dist/*.js',
    ],
    theme: {
        extend: {},
    },
    darkMode: 'selector',
    plugins: [
        require('preline/plugin'),
    ],
}

