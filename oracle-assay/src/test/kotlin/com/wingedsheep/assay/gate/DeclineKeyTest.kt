package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleFace
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * The three keyings, on hand-built cards — what each one *claims about the same line*, which is the
 * only thing that distinguishes them and the whole reason there are three.
 */
class DeclineKeyTest : StringSpec({

    fun card(name: String, text: String, typeLine: String = "Creature — Angel") = OracleCard(
        name = name,
        oracleId = null,
        layout = "normal",
        setCode = "TST",
        scryfallKeywords = emptyList(),
        faces = listOf(OracleFace(name = name, oracleText = text, typeLine = typeLine)),
    )

    val touchstone = Touchstone()

    fun declined(name: String, text: String) =
        touchstone.assay(card(name, text)).lines.first { it.verdict == LineVerdict.DECLINED }

    "the tail starts where the parse stopped, not where the line did" {
        // The trigger prefix reads; the effect clause is what is missing. TOKEN names the one token,
        // SHAPE names the whole sentence *including* the half that already parsed, and TAIL names
        // exactly the construct that would have to exist for the line to get further.
        val line = declined("Probe", "Flying\nWhen ~ enters, frobnicate target creature.")

        DeclineKey.TAIL.of(line) shouldBe "frobnicate target creature."
        DeclineKey.SHAPE.of(line) shouldBe "When ~ enters, frobnicate target creature."
        DeclineKey.TOKEN.of(line) shouldBe "frobnicate"
    }

    "one missing construct under two payloads is one tail family and two shapes" {
        // This is the bias SHAPE has and TAIL does not. A family whose sentence continues into an
        // arbitrary payload gets a shape row per payload and never rises; the tail holds it together.
        val drawing = declined("Drawing", "When ~ enters, frobnicate target creature and draw a card.")
        val gaining = declined("Gaining", "When ~ enters, frobnicate target creature and gain 2 life.")

        DeclineKey.TAIL.of(drawing) shouldBe DeclineKey.TAIL.of(gaining)
        (DeclineKey.SHAPE.of(drawing) == DeclineKey.SHAPE.of(gaining)) shouldBe false
    }

    "the tail is the whole line when the parse read nothing, and that is not a defect" {
        // A line that dies at offset 0 has no tail short of itself, so TAIL degenerates to SHAPE
        // here — the honest answer, since nothing was read and so everything has to be written. It
        // is also why the modal bullets are the one large family the TOKEN ranking names best.
        val line = declined("Bullet", "• Frobnicate target creature.")

        DeclineKey.TAIL.of(line) shouldBe "• Frobnicate target …"
        DeclineKey.TOKEN.of(line) shouldBe "•"
    }

    "the tail collapses numbers and mana symbols so two printings are one family" {
        val red = declined("Red", "When ~ enters, frobnicate for {R}{R} and 2 life.")
        val green = declined("Green", "When ~ enters, frobnicate for {G}{G} and 3 life.")

        DeclineKey.TAIL.of(red) shouldBe DeclineKey.TAIL.of(green)
        DeclineKey.TAIL.of(red) shouldBe "frobnicate for {§}{§} …"
    }

    "the word count is a parameter, because it is a measurement rather than a constant" {
        val line = declined("Probe", "When ~ enters, frobnicate target creature.")

        DeclineKey.TAIL.of(line, tailWords = 1) shouldBe "frobnicate …"
        DeclineKey.TAIL.of(line, tailWords = 2) shouldBe "frobnicate target …"
        // No marker when nothing was cut: a truncated key must be distinguishable from a whole one.
        DeclineKey.TAIL.of(line, tailWords = 20) shouldBe "frobnicate target creature."
    }

    "only the token ranking gives up the sole-blocked count" {
        DeclineKey.TOKEN.namesWork shouldBe false
        DeclineKey.SHAPE.namesWork shouldBe true
        DeclineKey.TAIL.namesWork shouldBe true
    }

    "sole-blocked counts cards, not lines — every declined line has to fall in the one family" {
        val report = FinenessReport.builder()
            .add(touchstone.assay(card("One Blocker", "When ~ enters, frobnicate target creature.")))
            .add(touchstone.assay(card("Blocker And Keyword", "When ~ enters, frobnicate target creature.\nFlying")))
            .add(
                touchstone.assay(
                    card(
                        "Two Blockers",
                        "When ~ enters, frobnicate target creature.\nWhen ~ dies, blorp target creature.",
                    )
                )
            )
            .build()

        val frobnicate = report.declines(DeclineKey.TAIL).single { it.key.startsWith("frobnicate") }
        frobnicate.cards shouldBe 3
        // The third card declines somewhere else too, so writing this family alone leaves it blocked.
        frobnicate.soleBlocked shouldBe 2
        report.declines(DeclineKey.TOKEN).first().soleBlocked shouldBe null
    }

    "--rank takes the key by name, and refuses one it does not know" {
        DeclineKey.byName("tail") shouldBe DeclineKey.TAIL
        DeclineKey.byName("TAIL") shouldBe DeclineKey.TAIL
        DeclineKey.byName("sentence") shouldBe null
        DeclineKey.byName(null) shouldBe null
    }
})
