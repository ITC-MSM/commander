package com.wingedsheep.mtg.sets

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

/**
 * Pins the art order of every set's basic lands: each land type leads with the set's **standard**
 * art, then its remaining treatments in printed order.
 *
 * This matters because limited deck building takes exactly one printing per type
 * (`BoosterGenerator.getBasicLands`) and it must be the plain booster art, not a full-art or
 * borderless treatment. `MtgSet.basicLands` is discovered reflectively, so before
 * [com.wingedsheep.sdk.model.BasicLandArt] ordering was applied the within-type order was whatever
 * the JVM happened to yield — meaning a drafted deck could come out full-art on one run and regular
 * on the next.
 */
class BasicLandArtOrderTest : FunSpec({

    test("every set orders each basic land type standard-art first") {
        val setsWithBasics = MtgSetCatalog.all.filter { it.basicLands.isNotEmpty() }
        setsWithBasics.shouldNotBeEmpty()

        assertSoftly {
            for (set in setsWithBasics) {
                for ((landName, variants) in set.basicLands.groupBy { it.name }) {
                    val numbers = variants.map { it.metadata.collectorNumber }
                    withClue("${set.code} $landName art order (collector numbers $numbers)") {
                        // Ascending collector number is what puts the main-set booster art ahead of
                        // the treatments numbered above the set's card count.
                        numbers shouldBe numbers.sortedBy { it?.toIntOrNull() ?: Int.MAX_VALUE }
                    }
                }
            }
        }
    }

    test("every sealed-supported set that prints a basic offers an in-booster printing of it") {
        // BoosterGenerator.getBasicLands keeps in-booster variants only. A set whose every printing
        // of a basic is marked non-booster (Scryfall reports `booster: false` for all of a set's
        // basics before release) hands limited deck building no lands of that type at all. Snow
        // basics and Wastes are opened, not handed out, so only the five regular types are held
        // to this.
        val suppliedTypes = setOf("Plains", "Island", "Swamp", "Mountain", "Forest")
        val setsWithBasics = MtgSetCatalog.all.filter { it.sealedSupported && it.basicLands.isNotEmpty() }

        assertSoftly {
            for (set in setsWithBasics) {
                for ((landName, variants) in set.basicLands.filter { it.name in suppliedTypes }.groupBy { it.name }) {
                    withClue("${set.code} $landName (collector numbers ${variants.map { it.metadata.collectorNumber }})") {
                        variants.any { it.metadata.inBooster } shouldBe true
                    }
                }
            }
        }
    }

    test("sets that print both regular and special art lead with the regular art") {
        // Each pair is (set code, land name) -> the collector number of the plain booster art, with
        // the treatment it must beat noted. Read off each set's basic-land file header.
        val expected = mapOf(
            ("BLB" to "Plains") to "262", // vs full-art 369-370
            ("LTR" to "Forest") to "270", // vs full-art Middle-earth map 280-281
            ("FIN" to "Island") to "297", // vs borderless 573
            ("EOE" to "Swamp") to "264", // vs extended art 369
            ("TDM" to "Mountain") to "275", // vs the non-booster Dragon's Eye full-art 290
            ("OTJ" to "Plains") to "272", // its one booster art, vs non-booster 277-278
            ("DSK" to "Forest") to "276",
            ("FDN" to "Plains") to "272",
            ("SOS" to "Plains") to "267",
            ("POR" to "Plains") to "196",
            ("FRA" to "Plains") to "281", // vs non-booster treatments 382-384
        )

        assertSoftly {
            for ((key, standardNumber) in expected) {
                val (setCode, landName) = key
                val set = MtgSetCatalog.all.single { it.code == setCode }
                withClue("$setCode $landName") {
                    // Mirrors BoosterGenerator.getBasicLands: in-booster variants only, first wins.
                    set.basicLands
                        .filter { it.name == landName && it.metadata.inBooster }
                        .first().metadata.collectorNumber shouldBe standardNumber
                }
            }
        }
    }
})
