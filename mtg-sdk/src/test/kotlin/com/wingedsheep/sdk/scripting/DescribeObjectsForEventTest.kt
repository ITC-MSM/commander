package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain

/**
 * [describeObjectsForEvent] renders the recipient half of a batch event wording ("Whenever you put
 * one or more +1/+1 counters on **one or more other Heroes you control**"). It is the only plural
 * describer in the SDK, and it feeds [EventPattern.CountersPlacedEvent.description] — which every
 * batch pattern computes at card-load time whether or not the card overrides its ability text.
 *
 * It shipped with three KDoc examples and no test, and one of them was wrong in two ways at once:
 * the claimed word order was backwards, and the head noun of the only shipped batch card
 * ("Hero") pluralized to "Heros". Hence this file: the documented examples are executable, and the
 * irregular table is checked against the head nouns the `Subtype` catalog can actually produce.
 */
class DescribeObjectsForEventTest : DescribeSpec({

    describe("pluralizeHeadNoun") {

        it("applies the regular English rules") {
            pluralizeHeadNoun("creature") shouldBe "creatures"
            pluralizeHeadNoun("permanent") shouldBe "permanents"
            pluralizeHeadNoun("Goblin") shouldBe "Goblins"
            pluralizeHeadNoun("Rhino") shouldBe "Rhinos"
            pluralizeHeadNoun("Hippo") shouldBe "Hippos"
            pluralizeHeadNoun("Ooze") shouldBe "Oozes"
        }

        it("adds -es after a sibilant") {
            pluralizeHeadNoun("Fox") shouldBe "Foxes"
            pluralizeHeadNoun("Phoenix") shouldBe "Phoenixes"
            pluralizeHeadNoun("Sphinx") shouldBe "Sphinxes"
            pluralizeHeadNoun("Lich") shouldBe "Liches"
        }

        it("leaves a noun that is already plural, or invariant in -s, alone") {
            pluralizeHeadNoun("creatures") shouldBe "creatures"
            pluralizeHeadNoun("Plains") shouldBe "Plains"
            pluralizeHeadNoun("Cyclops") shouldBe "Cyclops"
            pluralizeHeadNoun("Fungus") shouldBe "Fungus"
            pluralizeHeadNoun("Pegasus") shouldBe "Pegasus"
        }

        it("uses the curated table for -f/-fe subtypes the regular rules get wrong") {
            pluralizeHeadNoun("Elf") shouldBe "Elves"
            pluralizeHeadNoun("Wolf") shouldBe "Wolves"
            pluralizeHeadNoun("Werewolf") shouldBe "Werewolves"
            pluralizeHeadNoun("Dwarf") shouldBe "Dwarves"
        }

        it("uses the curated table for the genuinely irregular subtypes") {
            // The one this test exists for: MSH's own "one or more other Heroes you control".
            pluralizeHeadNoun("Hero") shouldBe "Heroes"
            pluralizeHeadNoun("Mouse") shouldBe "Mice"
            pluralizeHeadNoun("Class") shouldBe "Classes"
            pluralizeHeadNoun("Homunculus") shouldBe "Homunculi"
            pluralizeHeadNoun("Octopus") shouldBe "Octopuses"
        }

        it("keeps WotC's invariant plurals invariant") {
            pluralizeHeadNoun("Fish") shouldBe "Fish"
            pluralizeHeadNoun("Jellyfish") shouldBe "Jellyfish"
            pluralizeHeadNoun("Merfolk") shouldBe "Merfolk"
            pluralizeHeadNoun("Treefolk") shouldBe "Treefolk"
            pluralizeHeadNoun("Moonfolk") shouldBe "Moonfolk"
            pluralizeHeadNoun("Kithkin") shouldBe "Kithkin"
            pluralizeHeadNoun("Kor") shouldBe "Kor"
            pluralizeHeadNoun("Myr") shouldBe "Myr"
            pluralizeHeadNoun("Eldrazi") shouldBe "Eldrazi"
            pluralizeHeadNoun("Kavu") shouldBe "Kavu"
            pluralizeHeadNoun("Kree") shouldBe "Kree"
        }
    }

    describe("describeObjectsForEvent") {

        it("renders the KDoc's documented examples") {
            describeObjectsForEvent(GameObjectFilter.Creature) shouldBe "creatures"
            describeObjectsForEvent(GameObjectFilter.Creature.youControl()) shouldBe
                "creatures you control"
            describeObjectsForEvent(
                GameObjectFilter.Creature.youControl().withSubtype(Subtype.HERO)
            ) shouldBe "creature Heroes you control"
            describeObjectsForEvent(
                GameObjectFilter.Creature.youControl().withSubtype(Subtype.ELF)
            ) shouldBe "creature Elves you control"
        }

        it("pluralizes only the head noun, leaving the qualifiers singular") {
            describeObjectsForEvent(
                GameObjectFilter.Creature.withSubtype(Subtype.WOLF)
            ) shouldBe "creature Wolves"
        }

        it("keeps the controller as a suffix, never a prefix") {
            val rendered = describeObjectsForEvent(GameObjectFilter.Creature.youControl())
            rendered shouldBe "creatures you control"
            rendered shouldNotContain "you control creature"
        }

        it("falls back to the generic wording for an unconstrained filter") {
            describeObjectsForEvent(GameObjectFilter.Any) shouldBe "cards or permanents"
        }
    }

    describe("CountersPlacedEvent(batch = true).description") {

        /**
         * The end-to-end string the batch template produces. Invisible Woman, Sue Storm overrides
         * her ability text, so this is the wording a *future* batch card without an override
         * inherits — which is exactly why it needs pinning.
         */
        it("reads as English for the shipped batch filter") {
            EventPattern.CountersPlacedEvent(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                filter = GameObjectFilter.Creature.youControl().withSubtype(Subtype.HERO),
                placedBy = Player.You,
                batch = true,
            ).description shouldBe
                "you put one or more +1/+1 counters on one or more creature Heroes you control"
        }

        it("differs from the per-permanent template only in the recipient's multiplicity") {
            val perPermanent = EventPattern.CountersPlacedEvent(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                filter = GameObjectFilter.Creature.youControl(),
                placedBy = Player.You,
                batch = false,
            ).description
            val batched = EventPattern.CountersPlacedEvent(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                filter = GameObjectFilter.Creature.youControl(),
                placedBy = Player.You,
                batch = true,
            ).description

            perPermanent shouldBe "you put one or more +1/+1 counters on a creature you control"
            batched shouldBe
                "you put one or more +1/+1 counters on one or more creatures you control"
        }
    }
})
