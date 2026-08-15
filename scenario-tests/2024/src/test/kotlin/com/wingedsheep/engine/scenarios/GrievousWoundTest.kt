package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

/**
 * Grievous Wound (DSK #102) — proves the "enchant player" aura subsystem.
 *
 * "Enchant player
 *  Enchanted player can't gain life.
 *  Whenever enchanted player is dealt damage, they lose half their life, rounded up."
 *
 * Exercises: an Aura attaching to a player (not a permanent) and surviving state-based actions; a
 * PreventLifeGain scoped to Player.EnchantedPlayer; and a `takesDamage(binding = ATTACHED)` trigger
 * firing when the enchanted *player* is dealt damage.
 */
class GrievousWoundTest : FunSpec({

    // "Target player gains 5 life." — probes whether the enchanted player's life gain is locked.
    val TargetGainsFiveLife = CardDefinition.instant(
        name = "Bestow Five Life",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "Target player gains 5 life.",
        script = CardScript.spell(
            GainLifeEffect(5, EffectTarget.PlayerRef(Player.TargetPlayer)),
            com.wingedsheep.sdk.dsl.Targets.Player,
        ),
    )

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TargetGainsFiveLife))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun findOnBattlefield(driver: GameTestDriver, name: String): EntityId? =
        driver.state.getBattlefield().firstOrNull {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == name
        }

    fun castGrievousWoundOn(driver: GameTestDriver, caster: EntityId, victim: EntityId) {
        driver.giveMana(caster, Color.BLACK, 5)
        val gw = driver.putCardInHand(caster, "Grievous Wound")
        driver.castSpellWithTargets(caster, gw, listOf(ChosenTarget.Player(victim))).error shouldBe null
        driver.bothPass()
    }

    test("the Aura attaches to the enchanted player and survives state-based actions") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        castGrievousWoundOn(driver, caster = p1, victim = p2)

        val aura = findOnBattlefield(driver, "Grievous Wound")
        aura.shouldNotBeNull()
        driver.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId shouldBe p2
    }

    test("enchanted player can't gain life") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        castGrievousWoundOn(driver, caster = p1, victim = p2)

        // p1 tries to make p2 gain 5 life — prevented.
        val gain = driver.putCardInHand(p1, "Bestow Five Life")
        driver.giveMana(p1, Color.WHITE, 1)
        driver.castSpellWithTargets(p1, gain, listOf(ChosenTarget.Player(p2))).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(p2) shouldBe 20
    }

    test("when the enchanted player is dealt damage they lose half their life, rounded up") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        castGrievousWoundOn(driver, caster = p1, victim = p2)

        // Lightning Bolt deals 3 to p2 (20 -> 17); the trigger then halves 17 rounded up = 9 (17 -> 8).
        val bolt = driver.putCardInHand(p1, "Lightning Bolt")
        driver.giveMana(p1, Color.RED, 1)
        driver.castSpellWithTargets(p1, bolt, listOf(ChosenTarget.Player(p2))).error shouldBe null
        var guard = 0
        while (guard++ < 10 && !(driver.state.stack.isEmpty() && driver.state.pendingDecision == null)) {
            driver.bothPass()
        }

        driver.getLifeTotal(p2) shouldBe 8
    }
})
