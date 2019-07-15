package com.milushev.imagesearch.data.source.exception

class GenericServerException(msg: String? = null) : Exception(msg)

class ResponseParsingException(msg: String? = null) : Exception(msg)

class NoInternetConnectivityException : Exception()