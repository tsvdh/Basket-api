# Basket API

This project is the API that the Basket launcher provides for its apps and also uses itself.

Several pre-built JavaFX utilities are included in the API.

---

### Structure

Some assumptions are made about the structure of your project:

- All resources files are in a directory tagged as a resources folder.
- Your resource folders are opened to the api module `basket.api` in the `module-info.java` file.
- Data `.json` files are in the `data` folder.
  - `_info.json` is a reserved file name, do not use it yourself.
- Optionally, you can put `settings.json` in the data folder to be used as standard settings.
- Images or other visual data are in the `images` folder.
- The main icon of your app is called `icon.png`
- Any style files `.css`, `.ttf` are in the `style` folder.

---

### Config file

In order to have Basket run your app, a file called `basketConfig.json` must be present in the root of the folder you upload.

The following properties are in the file:

- `executablePath` *(required)*: path of the root of the folder to the file to execute, seperated with `/`.
