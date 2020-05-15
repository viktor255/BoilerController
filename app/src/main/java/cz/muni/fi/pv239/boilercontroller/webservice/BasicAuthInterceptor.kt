package cz.muni.fi.pv239.boilercontroller.webservice

import okhttp3.Interceptor


class BasicAuthInterceptor(private var token: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        val url = request.url.newBuilder().addQueryParameter("token", token).build()
        request = request.newBuilder().url(url).build();
        return chain.proceed(request)
    }
}