package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CycleCard
import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.mechanics.SplitSecond
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.MorphDataComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Split second (CR 702.61) in the engine.
 *
 * "As long as this spell is on the stack, players can't cast other spells or activate abilities
 * that aren't mana abilities" (702.61a); mana abilities and special actions stay legal, and
 * triggered abilities trigger and are put on the stack as normal (702.61b). Both halves of the
 * offer/accept contract are checked: the legal-action enumerator must not *offer* a locked action,
 * and validation must *refuse* one submitted anyway.
 */
class SplitSecondKeywordScenarioTest : FunSpec({

    val SuddenJolt = card("Sudden Jolt") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Split second\nSudden Jolt deals 2 damage to any target."
        keywords(Keyword.SPLIT_SECOND)
        spell {
            val t = target("target", Targets.Any)
            effect = Effects.DealDamage(2, t)
        }
    }

    val TestPinger = card("Test Pinger") {
        manaCost = "{R}"
        typeLine = "Creature — Human Wizard"
        power = 1
        toughness = 1
        oracleText = "{T}: This creature deals 1 damage to any target."
        activatedAbility {
            cost = Costs.Tap
            val t = target("target", Targets.Any)
            effect = Effects.DealDamage(1, t)
        }
    }

    val SpellWatcher = card("Spell Watcher") {
        manaCost = "{W}"
        typeLine = "Enchantment"
        oracleText = "Whenever a player casts a spell, you gain 1 life."
        triggeredAbility {
            trigger = Triggers.AnyPlayerCastsSpell
            effect = Effects.GainLife(1)
        }
    }

    val TestCycler = card("Test Cycler") {
        manaCost = "{2}{R}"
        typeLine = "Instant"
        oracleText = "Test Cycler deals 1 damage to any target.\nCycling {R}"
        keywordAbility(KeywordAbility.cycling("{R}"))
        spell {
            val t = target("target", Targets.Any)
            effect = Effects.DealDamage(1, t)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SuddenJolt, TestPinger, SpellWatcher, TestCycler))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Active player casts Sudden Jolt at the opponent and passes, handing the opponent priority. */
    fun GameTestDriver.castJoltAndPass(me: EntityId, opponent: EntityId): EntityId {
        val jolt = putCardInHand(me, "Sudden Jolt")
        giveMana(me, Color.RED, 1)
        castSpell(me, jolt, listOf(opponent)).error shouldBe null
        passPriority(me)
        assertPriority(opponent)
        return jolt
    }

    test("no player may cast a spell while a split-second spell is on the stack") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)

        driver.castJoltAndPass(me, opponent)
        SplitSecond.isLocked(driver.state, driver.cardRegistry) shouldBe true

        driver.legalActions(opponent).filter { it.action is CastSpell }.shouldBeEmpty()
        driver.submitExpectFailure(
            CastSpell(opponent, bolt, listOf(ChosenTarget.Player(me)))
        )
        driver.assertStackSize(1)
    }

    test("the caster is locked out too — split second binds every player") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val jolt = driver.putCardInHand(me, "Sudden Jolt")
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 2)

        driver.castSpell(me, jolt, listOf(opponent)).error shouldBe null
        driver.assertPriority(me)
        driver.legalActions(me).filter { it.action is CastSpell }.shouldBeEmpty()
        driver.submitExpectFailure(CastSpell(me, bolt, listOf(ChosenTarget.Player(opponent))))
    }

    test("non-mana activated abilities, cycling included, are locked out") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val pinger = driver.putCreatureOnBattlefield(opponent, "Test Pinger")
        driver.removeSummoningSickness(pinger)
        val cycler = driver.putCardInHand(opponent, "Test Cycler")
        driver.giveMana(opponent, Color.RED, 1)

        driver.castJoltAndPass(me, opponent)

        val offers = driver.legalActions(opponent)
        offers.filter { it.action is ActivateAbility && !it.isManaAbility }.shouldBeEmpty()
        offers.filter { it.action is CycleCard }.shouldBeEmpty()

        val pingAbility = driver.cardRegistry.getCard("Test Pinger")!!.script.activatedAbilities.single()
        driver.submitExpectFailure(
            ActivateAbility(opponent, pinger, pingAbility.id, targets = listOf(ChosenTarget.Player(me)))
        )
        driver.submitExpectFailure(CycleCard(opponent, cycler))
        driver.isTapped(pinger) shouldBe false
    }

    test("mana abilities stay legal (CR 702.61b)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val elves = driver.putCreatureOnBattlefield(opponent, "Llanowar Elves")
        driver.removeSummoningSickness(elves)

        driver.castJoltAndPass(me, opponent)

        val manaOffer = driver.legalActions(opponent)
            .firstOrNull { it.isManaAbility && (it.action as? ActivateAbility)?.sourceId == elves }
        manaOffer shouldNotBe null
        driver.submit(manaOffer!!.action).error shouldBe null
        driver.isTapped(elves) shouldBe true
    }

    test("special actions stay legal — a morph can still be turned face up (CR 702.61b)") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val morph = driver.putCreatureOnBattlefield(opponent, "Morph Test Creature")
        val morphCost = driver.cardRegistry.getCard("Morph Test Creature")!!
            .keywordAbilities.filterIsInstance<KeywordAbility.Morph>().single().morphCost
        driver.replaceState(driver.state.updateEntity(morph) { c ->
            c.with(FaceDownComponent).with(MorphDataComponent(morphCost, "Morph Test Creature"))
        })
        driver.giveMana(opponent, Color.WHITE, 2)

        driver.castJoltAndPass(me, opponent)

        driver.legalActions(opponent).filter { it.action is TurnFaceUp }.shouldNotBeEmpty()
        driver.submit(TurnFaceUp(opponent, morph)).error shouldBe null
        driver.state.getEntity(morph)?.get<FaceDownComponent>() shouldBe null
    }

    test("triggered abilities still trigger and go on the stack above the split-second spell") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.putPermanentOnBattlefield(me, "Spell Watcher")
        val jolt = driver.putCardInHand(me, "Sudden Jolt")
        driver.giveMana(me, Color.RED, 1)

        driver.castSpell(me, jolt, listOf(opponent)).error shouldBe null
        driver.assertStackSize(2)

        driver.bothPass() // the trigger resolves
        driver.assertLifeTotal(me, 21)
        driver.bothPass() // Sudden Jolt resolves
        driver.assertLifeTotal(opponent, 18)
    }

    test("the lock lifts once the split-second spell leaves the stack") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val jolt = driver.putCardInHand(me, "Sudden Jolt")
        driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 2)

        driver.castSpell(me, jolt, listOf(opponent)).error shouldBe null
        driver.legalActions(me).filter { it.action is CastSpell }.shouldBeEmpty()

        driver.bothPass()
        driver.assertStackSize(0)
        driver.assertLifeTotal(opponent, 18)
        SplitSecond.isLocked(driver.state, driver.cardRegistry) shouldBe false
        driver.assertPriority(me)
        driver.legalActions(me).filter { it.action is CastSpell }.shouldNotBeEmpty()
    }
})
