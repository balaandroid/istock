package com.fertail.istock

import io.reactivex.Observable
import org.json.JSONException
import org.json.JSONObject

abstract class BaseUseCase<T> {

    abstract fun createObservable(data: HashMap<String, Any>? = null): Observable<T>


    fun observable(withData: HashMap<String, Any>? = null): Observable<T> = createObservable(withData)

   /* @Throws(JSONException::class)
    open fun jsonToMap(t: String?): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        val jObject = JSONObject(t)
        val keys: Iterator<*> = jObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            val value: String = jObject.getString(key)
            map[key] = value
        }
       return map
    }*/

}