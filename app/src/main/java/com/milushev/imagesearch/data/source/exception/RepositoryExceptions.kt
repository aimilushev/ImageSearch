package com.milushev.imagesearch.data.source.exception

import java.lang.Exception

class GenericServerException(msg: String? = null) : Exception(msg)

class ResponseParsingException(msg: String? = null) : Exception(msg)

class NoInternetConnectivityException : Exception()