package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.Earthquake
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Ali from Cairo (Arabian Nights, {2}{R}{R}, 0/1 Human).
 *
 * Oracle: "Damage that would reduce your life total to less than 1 reduces it to 1
 * instead."
 *
 * Mechanically a damage-only life-loss floor (`LifeLossFloor`): damage is dealt at
 * full amount, lifelink and damage-dealt triggers still see the full amount, but
 * the life-total reduction is capped so Ali's controller stays at ≥ 1. Non-damage
 * life loss is unaffected.
 */
class AliFromCairoTest : FunSpec({

    val LoseFiveLifeSpell = CardDefinition.sorcery(
        name = "Drain Five",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "Target player loses 5 life.",
        script = CardScript.spell(
            effect = LoseLifeEffect(5, EffectTarget.BoundVariable("target")),
            TargetPlayer(id = "target"),
        ),
    )

    // 5/5 lifelink vanilla — used to assert the damage event still fires at the
    // unmodified amount even when Ali's floor caps the life loss to zero.
    val LifelinkBrute = CardDefinition.creature(
        name = "Lifelink Brute",
        manaCost = ManaCost.parse("{4}{W}"),
        subtypes = setOf(Subtype("Ox")),
        power = 5,
        toughness = 5,
        oracleText = "Lifelink",
        keywords = setOf(Keyword.LIFELINK),
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LoseFiveLifeSpell, LifelinkBrute, Earthquake))
        return driver
    }

    test("lethal damage to controller is floored to 1 life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.setLifeTotal(you, 3)

        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Player(you))).error shouldBe null
        driver.bothPass()

        driver.assertLifeTotal(you, 1)
    }

    test("non-lethal damage is unchanged") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.setLifeTotal(you, 10)

        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Player(you))).error shouldBe null
        driver.bothPass()

        driver.assertLifeTotal(you, 7)
    }

    test("opponent is not protected — Ali only floors his controller's life") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.setLifeTotal(opponent, 2)

        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Player(opponent))).error shouldBe null
        driver.bothPass()

        // Opponent took the full 3 — Ali's floor did not apply to them.
        driver.assertGameOver(expectedWinner = you)
    }

    test("direct life loss (not from damage) bypasses Ali's floor") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.setLifeTotal(you, 3)

        // Drain Five is sorcery-speed life loss with no damage involved — Ali's floor
        // must NOT engage, so 3 - 5 = -2 and you lose.
        driver.giveMana(you, Color.BLACK, 1)
        val drain = driver.putCardInHand(you, "Drain Five")
        driver.castSpellWithTargets(you, drain, listOf(ChosenTarget.Player(you))).error shouldBe null
        driver.bothPass()

        driver.assertGameOver(expectedWinner = opponent)
    }

    test("once Ali leaves the battlefield, his floor stops protecting his controller") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ali = driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.moveToGraveyard(ali)

        driver.setLifeTotal(you, 3)
        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Player(you))).error shouldBe null
        driver.bothPass()

        // No Ali on the battlefield → 3 damage is taken in full → you lose.
        driver.assertGameOver(expectedWinner = opponent)
    }

    // Per the printed ruling: "This effect does not prevent damage, it prevents the damage
    // from turning into loss of life. So the full damage is dealt (and abilities that trigger
    // on damage being dealt still trigger), but the full loss of life is not applied."
    test("damage event still fires at full amount — lifelink sees unfloored damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val attacker = driver.activePlayer!!
        val ali = driver.getOpponent(attacker)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(ali, "Ali from Cairo")
        val brute = driver.putCreatureOnBattlefield(attacker, "Lifelink Brute")
        driver.removeSummoningSickness(brute)
        driver.setLifeTotal(ali, 1)
        driver.setLifeTotal(attacker, 20)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(brute), ali)
        driver.bothPass()  // declare blockers (none)
        driver.bothPass()  // first strike step (no first strikers)
        driver.bothPass()  // combat damage step

        // Floor caps life loss to 0 (currentLife - floor = 1 - 1 = 0) → Ali still at 1.
        driver.assertLifeTotal(ali, 1)
        // Lifelink reads the unmodified 5 damage from the DamageDealtEvent → attacker gains 5.
        // If the engine had instead routed Ali through "prevent damage", this would be 20.
        driver.assertLifeTotal(attacker, 25)
    }

    // Per the printed ruling: "The ability works up until Ali enters the graveyard, so if he
    // takes lethal damage or is destroyed at the same time you take damage, the ability
    // helps you." Ali is still on the battlefield throughout the damage event (SBAs don't
    // fire until the whole effect resolves), so the floor applies even when the same effect
    // would kill him.
    test("Ali still protects when he and his controller take lethal damage in the same event") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Ali from Cairo")
        driver.setLifeTotal(you, 5)
        driver.setLifeTotal(opponent, 20)

        // Earthquake deals X damage to each creature without flying and each player.
        // X = 5 → 5 to Ali (lethal for the 0/1), 5 to you (would be lethal), 5 to opponent.
        driver.giveMana(you, Color.RED, 6)
        val eq = driver.putCardInHand(you, "Earthquake")
        driver.castXSpell(you, eq, xValue = 5).error shouldBe null
        driver.bothPass()

        // Ali was on the battlefield throughout the damage event → floor applied to "you".
        // SBAs then put Ali in the graveyard for lethal marked damage.
        driver.assertLifeTotal(you, 1)
        driver.assertInGraveyard(you, "Ali from Cairo")
        driver.assertLifeTotal(opponent, 15)
    }
})
