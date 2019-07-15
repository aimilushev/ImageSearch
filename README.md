ImageSearch application
=========================

This is a demo application showcasing an image search functionality. The data is retrieved from backend web service and is presented in a 3 column grid. The images count may be really big, so they are retrieved on chunks, simulating an infinite scroll.
The data is retrieved from flickr API

 Application dependencies
 -------------------------------
This application is integrating the latest android best practices. It's written in kotlin and is based on Android Jetpack components including:
- ViewModel
- LiveData
- Paging
- Lifecycle
- ConstraintLayout

Asyncronous tasks are made via kotlin coroutines.
No 3rd party dependencies are used.

Application components and implementation details
--------------------------------------------------------

#### UI
SearchFragment is the screen responsible for visualising the search bar and search results
#### Presentation
 SearchViewModel is the viewModel feeding the fragment with search results and interacting with data layer to retrieve search results
#### Data
 - NetworkPhotosDataSource: class implementing PhotosDataSource used to make network API calls.
 - DefaultPhotosRepository: class implementing PhotosRepository used as a facade to hide data retrieving implementation. Currently it transparently calls NetworkPhotosDataSource, but can easily be extended to add some sort of caching with minimal changes adding another PhotosDataSource implementation
 - PhotosPageDataSource : data source based on the Paging component used to retrieve data from the repository for the current or next "page" of photos

#### Others
 - ImageLoader: class used for loading and caching images from the network
 - JsonMapper: class used for json to object conversion
 - Injector: class used to enhance dependency injection, giving the possibility to replace some implementations during tests

## Unit tests
 Unit tests are based on Junit 4, Mockito and Kotlinx Coroutines Test

 Possible improvements
 ---------------------
 - Implement retry mechanism when a web service call fails
 - Implement image cache on file system
 - Add voice search and search suggestions
 - Write some integration tests