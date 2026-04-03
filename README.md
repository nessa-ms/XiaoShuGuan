## XiaoShuGuan

### Description
#### Personal Library Desktop Application <br> 
Manage your personal library by adding books as epub files (or not yet implemented manually). <br>
Edit the book title, author name, genres and book cover. <br>
Add your personal notes to a book. <br>
Track your reading progress. <br>
Filter your books by various categories such as author, genre (and not yet implemented reading status). <br>
Sort your books by various categories such as title, author, genre etc. <br>
Use XiaoShuGuan to load books into your kobo or tolino ereader via USB connection. (not yet implemented) <br>

### Preview
<div align="center">
  <img width="83%" src="preview/MainTable.png" alt="Sample image">
</div>

<div align="center" style="display: flex; justify-content: center; gap: 20px; margin-top: 20px;">
  <div style="width: 40%;">
    <img width="100%" src="preview/BookDetails.png" alt="Sample image">
  </div>
  <div style="width: 40%; display: flex; flex-direction: column; gap: 10px;">
    <img width="100%" src="preview/Goodreads.png" alt="Sample image">
    <img width="100%" src="preview/GoodreadsConfirmation.png" alt="Sample image">
  </div>
</div>

### Installation

### Dependencies
<a href=https://mvnrepository.com/artifact/nl.siegmann.epublib/epublib-core/3.1>epublib-core by siegmann</a>

### Usage

### Test

### Project Progress
TODO
Functional
- implement read status filter

- implement adding a book manually (typing info and how to deal with epub not being available)

- implement changing book cover (for export to ereader)
- implement export to ereader

Aesthetics
- change javafx default ! and ? icons of popup windows
- Add custom cursor

### Author
vanessaduldier@gmx.de

### Licence 
This project is licensed under the  
**Creative Commons Attribution–NonCommercial 4.0 International (CC BY-NC 4.0)**.

You are free to use, study, and modify this project for **non-commercial purposes only**.

Commercial use is **not permitted** without explicit permission from the author.

Note this project contains a function to add genres to a book by pasting a goodreads link of said book. Goodreads is not an open api service, so commertial use of this function is not permitted. This is merely a personal project of mine.

© 2026 Vanessa Duldier
