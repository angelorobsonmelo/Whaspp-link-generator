package br.com.angelorobson.whatsapplinkgenerator.model.repositories

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.model.dtos.CountryDto
import br.com.angelorobson.whatsapplinkgenerator.model.entities.CountryEntity
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import br.com.angelorobson.whatsapplinkgenerator.model.services.CountryService
import io.reactivex.Observable
import javax.inject.Inject

class CountryRepository @Inject constructor(
    private val service: CountryService
) {

    fun getAllFromApi(): Observable<List<Country>> {
        return service.getAll()
            .map {
                it.map { countryDto ->
                    mapToCountry(countryDto)
                }
            }
    }

}

fun mapToCountry(dto: CountryDto): Country {
    return Country(
        countryFullName = dto.name,
        areaCode = dto.callingCodes.first(),
        flag = dto.flag,
        countryShortName = dto.alpha2Code
    )
}