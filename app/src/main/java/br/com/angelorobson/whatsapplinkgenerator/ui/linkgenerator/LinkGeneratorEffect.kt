package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

sealed class LinkGeneratorEffect

object ObserveCountries : LinkGeneratorEffect()

data class SaveHistory(
    val history: History
) : LinkGeneratorEffect()