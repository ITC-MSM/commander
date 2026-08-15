package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleFace
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

/**
 * The gate's own behaviour, on hand-built cards rather than on the corpus — the corpus run lives
 * in `just assay-gate`, which is a reporting tool, not a unit test.
 */
class TouchstoneTest : StringSpec({

    fun card(
        name: String,
        text: String,
        keywords: List<String> = emptyList(),
        typeLine: String = "Creature — Angel",
    ) = OracleCard(
        name = name,
        oracleId = null,
        layout = "normal",
        setCode = "TST",
        scryfallKeywords = keywords,
        faces = listOf(OracleFace(name = name, oracleText = text, typeLine = typeLine)),
    )

    val touchstone = Touchstone()

    "a keyword card round-trips byte-exact" {
        val result = touchstone.assay(card("Serra Angel", "Flying\nVigilance", listOf("Flying", "Vigilance")))

        result.lines.map { it.verdict } shouldBe listOf(LineVerdict.ROUND_TRIP, LineVerdict.ROUND_TRIP)
        result.roundTrips shouldBe true
        result.faces.single().restoreHolds shouldBe true
    }

    "a vanilla card round-trips trivially and counts as covered" {
        val result = touchstone.assay(card("Grizzly Bears", ""))

        result.covered shouldBe true
        result.inPhase1Scope shouldBe true
        result.faces.single().normalized.isVanilla shouldBe true
    }

    "reminder text is stripped before parsing and does not break the round trip" {
        val result = touchstone.assay(
            card(
                "Storm Crow",
                "Flying (This creature can't be blocked except by creatures with flying or reach.)",
                listOf("Flying"),
            )
        )

        result.roundTrips shouldBe true
        result.faces.single().glosses.single().verdict shouldBe GlossVerdict.MATCHED
    }

    "a printed gloss our model disagrees with is a finding, not a failure" {
        val result = touchstone.assay(card("Odd Crow", "Flying (This creature flies, obviously.)", listOf("Flying")))

        result.roundTrips shouldBe true
        result.faces.single().glosses.single().verdict shouldBe GlossVerdict.DIFFERED
    }

    "an alternate spelling is a variant: the model survives, the spelling normalizes" {
        val result = touchstone.assay(card("Nalathni Dragon", "Flying; banding", listOf("Flying", "Banding")))

        result.lines.single().verdict shouldBe LineVerdict.VARIANT
        result.lines.single().printed shouldBe "Flying, banding"
        result.covered shouldBe true
        result.roundTrips shouldBe false
    }

    // The uncovered line is an activated ability, which no rule reaches yet. It used to be an ETB
    // trigger, until the grammar learned to read one — a fixture for "text outside the grammar" has
    // to be replaced whenever the grammar catches up with it, and that is the healthy direction.
    "text outside the grammar declines and is attributed to the token it died on" {
        val result = touchstone.assay(card("Llanowar Elves", "Defender\n{T}: Add {G}."))

        result.lines.map { it.verdict } shouldBe listOf(LineVerdict.ROUND_TRIP, LineVerdict.DECLINED)
        result.lines.last().declineToken shouldBe "{T}:"
        result.covered shouldBe false
    }

    "an ability word is not a keyword-only card, even though Scryfall tags it as a keyword" {
        val result = touchstone.assay(
            card(
                "Domain Creature",
                "Domain — This creature gets +1/+1 for each basic land type among lands you control.",
                listOf("Domain"),
            )
        )

        result.inPhase1Scope shouldBe false
    }

    "a card with rules text beside its keywords is out of Phase 1 scope" {
        val result = touchstone.assay(
            card("Wall of Omens", "Defender\nWhen this creature enters, draw a card.", listOf("Defender"))
        )

        result.inPhase1Scope shouldBe false
    }

    "the report counts what it says it counts" {
        val report = FinenessReport.builder()
            .add(touchstone.assay(card("Serra Angel", "Flying\nVigilance", listOf("Flying", "Vigilance"))))
            .add(touchstone.assay(card("Grizzly Bears", "")))
            .add(touchstone.assay(card("Llanowar Elves", "Defender\n{T}: Add {G}.")))
            .build()

        report.cards shouldBe 3
        report.lineInstances shouldBe 5
        report.instancesByVerdict[LineVerdict.ROUND_TRIP] shouldBe 4
        report.instancesByVerdict[LineVerdict.DECLINED] shouldBe 1
        report.cardsCovered shouldBe 2
        report.clean shouldBe true
        report.declines.single().token shouldBe "{T}:"
        report.declines.single().cards shouldBe 1
    }

    "the report is locale-independent so it can be diffed and pasted" {
        val report = FinenessReport.builder().add(touchstone.assay(card("Grizzly Bears", ""))).build()
        report.render().contains("1000.0‰ (100.0%)") shouldBe true
    }

    "fineness is parts per thousand, and says so next to the percent it is not" {
        // 840.5‰ is 84.1%, not 84.05% and emphatically not 840%. The unit is a factor of ten away
        // from the one a reader reaches for, so both are printed.
        FinenessReport.permil(1439, 1712) shouldBe (840.5 plusOrMinus 0.05)
        val report = FinenessReport.builder()
            .add(touchstone.assay(card("Serra Angel", "Flying", listOf("Flying"))))
            .add(touchstone.assay(card("Llanowar Elves", "{T}: Add {G}.")))
            .build()
        report.render().contains("500.0‰ (50.0%)") shouldBe true
    }
})
