package com.milushev.imagesearch.data.source.exception

import java.lang.Exception

class GenericServerException : Exception()

class ResponseParsingException(msg: String?) : Exception(msg)

class NoInternetConnectivityException : Exception()