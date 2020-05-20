package br.com.angelorobson.whatsapplinkgenerator.model.services

import br.com.angelorobson.whatsapplinkgenerator.model.dtos.CountryDto
import io.reactivex.Observable
import retrofit2.http.GET

interface CountryService {

    @GET("all")
    fun getAll(): Observable<List<CountryDto>>
}