# serienchecker
an old project for keeping track of what series and episodes I have watched.
I had to change it recently for changes with imdb, so I just put it into a git when I was doing that.

known issues:
 * the window will sometimes jump around, can't be bothered to fix that
 * the tmdb scraping is rather volatile, due to changes happening regularly at tmdb (code for imdb exists, but breaks far too often)
 * with a clean database it'll crash and create an invalid archive. `--add-opens java.base/java.util=ALL-UNNAMED` does fix that
 * path recognition is for linux, has problem on windows and puts the archive wherever
