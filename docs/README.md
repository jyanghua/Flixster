# Flix
Flix is an app that allows users to browse movies from the [The Movie Database API](http://docs.themoviedb.apiary.io/#).

[Click here](https://github.com/jyanghua/Flixster/raw/master/docs/debug/Flixster.apk) to download the APK

## Flix Part 2

### User Stories

#### REQUIRED (10pts)

- [x] (8pts) Expose details of movie (ratings using RatingBar, popularity, and synopsis) in a separate activity.
- [x] (2pts) Allow video posts to be played in full-screen using the YouTubePlayerView.

#### BONUS

- [x] Trailers for popular movies are played automatically when the movie is selected (1 point).
  - [x] When clicking on a popular movie (i.e. a movie voted for more than 7.5 stars) the video should be played immediately.
  - [x] Less popular videos rely on the detailed page should show an image preview that can initiate playing a YouTube video.
- [x] Add a play icon overlay to popular movies to indicate that the movie can be played (1 point).
- [x] Apply the popular ButterKnife annotation library to reduce view boilerplate. (1 point)
- [x] Add a rounded corners for the images using the Glide transformations. (1 point)

### App Walkthough GIF

**Scrolling through the Recycler View**

<img src="/docs/gifs/Flixster_part2_recyclerview.gif"><br>

**Detailed view on a movie rated lower than 7.5**

<img src="/docs/gifs/Flixster_part2_detailed1.gif"><br>

**Detailed view with autoplay YouTube videos and orientation changes**

<img src="/docs/gifs/Flixster_part2_detailed2.gif"><br>

### Notes

The biggest challenge was managing configuration changes, especifically orientation when the YouTube video exited fullscreen mode. At the same time preserving the current state of the YouTube video.

## Open-source libraries used
- [Android Async HTTP](https://github.com/codepath/CPAsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [Butter Knife](https://jakewharton.github.io/butterknife/) - Field and method binding for Android views (Reduces boilerplate)
---

## Flix Part 1

### User Stories

#### REQUIRED (10pts)
- [x] (10pts) User can view a list of movies (title, poster image, and overview) currently playing in theaters from the Movie Database API.

#### BONUS
- [x] (2pts) Views should be responsive for both landscape/portrait mode.
   - [x] (1pt) In portrait mode, the poster image, title, and movie overview is shown.
   - [x] (1pt) In landscape mode, the rotated alternate layout should use the backdrop image instead and show the title and movie overview to the right of it.

- [x] (2pts) Display a nice default [placeholder graphic](https://guides.codepath.org/android/Displaying-Images-with-the-Glide-Library#advanced-usage) for each image during loading
- [x] (2pts) Improved the user interface by experimenting with styling and coloring.
- [x] (2pts) For popular movies (i.e. a movie voted for more than 5 stars), the full backdrop image is displayed. Otherwise, a poster image, the movie title, and overview is listed. Use Heterogenous RecyclerViews and use different ViewHolder layout files for popular movies and less popular ones.

### App Walkthough GIF

**Portrait view**

<img src="/docs/gifs/Flixster_part1_portrait.gif" width=350><br>

**Landscape view**

<img src="/docs/gifs/Flixster_part1_landscape.gif"><br>

### Notes
The hardest part was trying to modularize the Heterogeneous RecyclerViews, there might be a better way to do it without too much code redundancy, but in this case it both viewholders are treated as separate despite the minimal changes between them (Popular movie view holder vs regular movie view holder).

### Open-source libraries used

- [Android Async HTTP](https://github.com/codepath/CPAsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Androids
