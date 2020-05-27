package br.com.angelorobson.whatsapplinkgenerator.ui.share

import org.threeten.bp.LocalDateTime

fun getNow(): String {
    return LocalDateTime.now().toString()
}