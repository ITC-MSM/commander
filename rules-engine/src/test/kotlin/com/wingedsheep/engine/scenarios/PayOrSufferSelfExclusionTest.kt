package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * "Sacrifice/tap it unless you [cost]" — whether the source itself is a legal way to pay.
 *
 * The answer is the printed word "another", carried on the cost atom as `excludeSelf`.
 * `PayOrSufferExecutor` used to ignore that flag and drop the source from every candidate set,
 * which disagreed with `CostPaymentService` (which has always read it) and silently outlawed two
 * printed rulings — Public Thoroughfare's and Command Bridge's "you can tap it for its own
 * triggered ability" (2024-02-02).
 *
 * Both directions matter, so both are pinned here:
 *
 *  - `excludeSelf = false` (the default, and what a filter-matching source is printed as) must
 *    offer the source as a choice, and paying with it must work end to end;
 *  - `excludeSelf = true` must still keep the source out — and, when it is the *only* match, fall
 *    through to the suffer effect without ever raising a prompt.
 *
 * The tap and sacrifice paths are separate code in the executor, so each gets its own pair.
 */
class PayOrSufferSelfExclusionTest : FunSpec({

    /**
     * An artifact creature whose enters trigger reads "sacrifice it unless you sacrifice an
     * artifact" — the source matches its own filter, so it is legal fodder for itself.
     */
    fun selfSacrificer(name: String, excludeSelf: Boolean) = CardDefinition(
        name = name,
        manaCost = ManaCost.parse("{2}"),
        typeLine = TypeLine.artifactCreature(setOf(Subtype("Construct"))),
        oracleText = "When this creature enters, sacrifice it unless you sacrifice " +
            (if (excludeSelf) "another artifact." else "an artifact."),
        creatureStats = CreatureStats(3, 3),
        script = CardScript.creature(
            TriggeredAbility.create(
                trigger = EventPattern.ZoneChangeEvent(to = Zone.BATTLEFIELD),
                binding = TriggerBinding.SELF,
                effect = PayOrSufferEffect(
                    cost = if (excludeSelf) Costs.pay.SacrificeAnother(GameObjectFilter.Artifact)
                    else Costs.pay.Sacrifice(GameObjectFilter.Artifact),
                    suffer = SacrificeSelfEffect
                )
            )
        )
    )

    /**
     * The Public Thoroughfare shape reduced to its bones: an *untapped* artifact creature whose
     * enters trigger reads "sacrifice it unless you tap an untapped artifact you control".
     */
    fun selfTapper(name: String, excludeSelf: Boolean) = CardDefinition(
        name = name,
        manaCost = ManaCost.parse("{2}"),
        typeLine = TypeLine.artifactCreature(setOf(Subtype("Construct"))),
        oracleText = "When this creature enters, sacrifice it unless you tap " +
            (if (excludeSelf) "another untapped artifact you control." else "an untapped artifact you control."),
        creatureStats = CreatureStats(3, 3),
        script = CardScript.creature(
            TriggeredAbility.create(
                trigger = EventPattern.ZoneChangeEvent(to = Zone.BATTLEFIELD),
                binding = TriggerBinding.SELF,
                effect = PayOrSufferEffect(
                    cost = if (excludeSelf) Costs.pay.TapAnother(GameObjectFilter.Artifact)
                    else Costs.pay.Tap(GameObjectFilter.Artifact),
                    suffer = SacrificeSelfEffect
                )
            )
        )
    )

    val SelfSacrificer = selfSacrificer("Self Sacrificer", excludeSelf = false)
    val AnotherSacrificer = selfSacrificer("Another Sacrificer", excludeSelf = true)
    val SelfTapper = selfTapper("Self Tapper", excludeSelf = false)
    val AnotherTapper = selfTapper("Another Tapper", excludeSelf = true)

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        listOf(SelfSacrificer, AnotherSacrificer, SelfTapper, AnotherTapper).forEach(driver::registerCard)
        return driver
    }

    /** Cast [cardName] as the only artifact on an otherwise empty board and resolve to the trigger. */
    fun castAlone(driver: GameTestDriver, cardName: String): com.wingedsheep.sdk.model.EntityId {
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val card = driver.putCardInHand(active, cardName)
        driver.giveMana(active, Color.GREEN, 2)
        driver.castSpell(active, card).isSuccess shouldBe true
        driver.bothPass() // resolve the permanent
        driver.stackSize shouldBe 1
        driver.bothPass() // resolve the enters trigger
        return active
    }

    test("sacrifice: excludeSelf = false offers the source, and paying with it works") {
        val driver = createDriver()
        val active = castAlone(driver, "Self Sacrificer")
        val self = driver.findPermanent(active, "Self Sacrificer")!!

        val decision = driver.pendingDecision
        withClue("the source matches its own filter, so it is a legal choice") {
            decision.shouldBeInstanceOf<SelectCardsDecision>().options shouldContain self
        }

        driver.submitCardSelection(active, listOf(self))
        withClue("paying with itself costs the permanent — it is sacrificed as the cost, not as the suffer") {
            driver.findPermanent(active, "Self Sacrificer") shouldBe null
            driver.getGraveyardCardNames(active) shouldContain "Self Sacrificer"
        }
    }

    test("sacrifice: excludeSelf = true keeps the source out and falls through to the suffer") {
        val driver = createDriver()
        val active = castAlone(driver, "Another Sacrificer")

        withClue("'another' with no other artifact is unpayable, so no prompt is raised") {
            driver.pendingDecision shouldBe null
        }
        withClue("and the suffer effect sacrifices it") {
            driver.findPermanent(active, "Another Sacrificer") shouldBe null
            driver.getGraveyardCardNames(active) shouldContain "Another Sacrificer"
        }
    }

    test("sacrifice: excludeSelf = true still offers a genuine 'another'") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20), skipMulligans = true)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val other = driver.putCreatureOnBattlefield(active, "Artifact Creature")
        val card = driver.putCardInHand(active, "Another Sacrificer")
        driver.giveMana(active, Color.GREEN, 2)
        driver.castSpell(active, card).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        val self = driver.findPermanent(active, "Another Sacrificer")!!
        val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        withClue("the other artifact is offered") { decision.options shouldContain other }
        withClue("but the source is not — that is what 'another' means") {
            decision.options shouldNotContain self
        }

        driver.submitCardSelection(active, listOf(other))
        withClue("paying keeps the source on the battlefield") {
            driver.findPermanent(active, "Another Sacrificer") shouldBe self
        }
    }

    test("tap: excludeSelf = false offers the untapped source, and tapping it pays") {
        val driver = createDriver()
        val active = castAlone(driver, "Self Tapper")
        val self = driver.findPermanent(active, "Self Tapper")!!

        val decision = driver.pendingDecision
        withClue("an untapped source that matches its own filter can pay the tap cost") {
            decision.shouldBeInstanceOf<SelectCardsDecision>().options shouldContain self
        }

        driver.submitCardSelection(active, listOf(self))
        withClue("it survives, tapped — the Public Thoroughfare / Command Bridge ruling") {
            driver.findPermanent(active, "Self Tapper") shouldBe self
            driver.isTapped(self) shouldBe true
        }
    }

    test("tap: excludeSelf = true keeps the source out and falls through to the suffer") {
        val driver = createDriver()
        val active = castAlone(driver, "Another Tapper")

        withClue("'another' with no other untapped artifact is unpayable, so no prompt is raised") {
            driver.pendingDecision shouldBe null
        }
        withClue("and the suffer effect sacrifices it") {
            driver.findPermanent(active, "Another Tapper") shouldBe null
            driver.getGraveyardCardNames(active) shouldContain "Another Tapper"
        }
    }
})
