package com.wingedsheep.assay.corpus

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * The join [SetCards.contains] performs, on a hand-built list rather than on Scryfall — the fetch
 * itself is network and is exercised by `just assay-report --set POR`, not by a unit test.
 *
 * What is worth pinning here is the *shape* of the join, because it is the half that decides whether
 * a set filter is honest. The bug this replaced compared a card's representative printing to the set
 * code, which showed 53 of Portal's 200 cards.
 */
class SetMembershipTest : StringSpec({

    fun card(name: String, oracleId: String? = null, setCode: String? = "XXX") = OracleCard(
        name = name,
        oracleId = oracleId,
        layout = "normal",
        setCode = setCode,
        scryfallKeywords = emptyList(),
        faces = listOf(OracleFace(name = name, oracleText = "")),
    )

    val portal = SetCards(
        code = "por",
        cards = listOf(
            SetCard(oracleId = "oid-blaze", name = "Blaze"),
            SetCard(oracleId = "oid-raise", name = "Raise Dead"),
            SetCard(oracleId = null, name = "Wild Griffin"),
        ),
    )

    "a card joins its set by Oracle ID whatever printing the corpus shows it under" {
        // The whole point: Blaze's representative printing is BBD, and it is still a Portal card.
        portal.contains(card("Blaze", oracleId = "oid-blaze", setCode = "BBD")) shouldBe true
    }

    "a card the set does not contain stays out even when its Oracle ID is unknown" {
        portal.contains(card("Serra Angel", oracleId = null)) shouldBe false
    }

    "name is the fallback when the corpus carries no Oracle ID" {
        portal.contains(card("Wild Griffin", oracleId = null, setCode = "CN2")) shouldBe true
    }

    "the name fallback ignores case" {
        portal.contains(card("raise dead")) shouldBe true
    }

    "an Oracle ID the set does not list does not match a different card's ID" {
        portal.contains(card("Shock", oracleId = "oid-blaze")) shouldBe true
        portal.contains(card("Shock", oracleId = "oid-shock")) shouldBe false
    }

    "a two-faced name joins a set list that spells only the front face" {
        val list = SetCards("tst", listOf(SetCard(oracleId = null, name = "Delver of Secrets")))
        list.contains(card("Delver of Secrets // Insectile Aberration")) shouldBe true
    }

    "a set list that spells both faces joins a corpus entry spelling one" {
        val list = SetCards("tst", listOf(SetCard(oracleId = null, name = "Delver of Secrets // Insectile Aberration")))
        list.contains(card("Delver of Secrets")) shouldBe true
    }

    "size is the number of cards printed in the set, which is the filter's denominator" {
        portal.size shouldBe 3
    }

    "a code shorter than any real set code resolves to nothing without a request" {
        // 1,047 Scryfall sets, none with a code under three characters. A shorter string is a
        // half-typed prefix in the explorer's set box, and answering it locally is what stops a
        // debounced input firing a 404 at Scryfall per keystroke.
        SetMembership.of("p") shouldBe null
        SetMembership.of("po") shouldBe null
    }

    "the cache file is prefixed so it cannot collide with another tool's per-set file" {
        SetMembership.cacheFile("POR").name shouldBe "_setlist-por.tsv"
    }
})
