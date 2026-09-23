package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Tests for LifeLossEvent trigger support.
 *
 * Verifies that "Whenever you lose life" triggers fire correctly from:
 * - Direct life loss effects (e.g., "lose 3 life")
 * - Combat damage
 */
class LifeLossTriggerTest : FunSpec({

    val LifeLossDrawer = CardDefinition.enchantment(
        name = "Life Loss Drawer",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "Whenever you lose life, draw a card.",
        script = CardScript(
            triggeredAbilities = listOf(
                TriggeredAbility(
                    id = AbilityId.generate(),
                    trigger = Triggers.YouLoseLife.event,
                    binding = Triggers.YouLoseLife.binding,
                    effect = DrawCardsEffect(1)
                )
            )
        )
    )

    val LifeLossMirror = CardDefinition.enchantment(
        name = "Life Loss Mirror",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "Whenever you lose life, each opponent loses that much life.",
        script = CardScript(
            triggeredAbilities = listOf(
                TriggeredAbility(
                    id = AbilityId.generate(),
                    trigger = Triggers.YouLoseLife.event,
                    binding = Triggers.YouLoseLife.binding,
                    effect = LoseLifeEffect(
                        amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_LIFE_LOST),
                        target = EffectTarget.PlayerRef(Player.EachOpponent)
                    )
                )
            )
        )
    )

    val LoseThreeLife = CardDefinition.instant(
        name = "Lose Three Life",
        manaCost = ManaCost.parse("{B}"),
        oracleText = "You lose 3 life.",
        script = CardScript.spell(
            effect = LoseLifeEffect(
                amount = DynamicAmount.Fixed(3),
                target = EffectTarget.Controller
            )
        )
    )

    fun createDriver(vararg extraCards: CardDefinition): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + extraCards.toList())
        return driver
    }

    test("YouLoseLife triggers on direct life loss") {
        val driver = createDriver(LifeLossDrawer, LoseThreeLife)
        driver.initMirrorMatch(
            deck = Deck.of("Grizzly Bears" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Life Loss Drawer")

        val handSizeBefore = driver.getHand(activePlayer).size

        val spell = driver.putCardInHand(activePlayer, "Lose Three Life")
        driver.giveMana(activePlayer, Color.BLACK, 1)
        driver.castSpell(activePlayer, spell).outcome shouldBe Outcome.Done
        driver.bothPass() // resolve Lose Three Life (lose 3 life, trigger fires)
        driver.bothPass() // resolve triggered ability (draw a card)

        driver.assertLifeTotal(activePlayer, 17)
        driver.getHand(activePlayer).size shouldBe handSizeBefore + 1
    }

    test("YouLoseLife triggers on combat damage") {
        val driver = createDriver(LifeLossDrawer)
        driver.initMirrorMatch(
            deck = Deck.of("Grizzly Bears" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(opponent, "Life Loss Drawer")

        val bearsId = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bearsId)

        val handSizeBefore = driver.getHand(opponent).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(bearsId), opponent)

        // No blockers — advance to combat damage
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)

        // Combat damage deals 2 to opponent, triggering life loss
        driver.bothPass() // process combat damage
        driver.bothPass() // resolve triggered ability (draw a card)

        driver.assertLifeTotal(opponent, 18)
        driver.getHand(opponent).size shouldBe handSizeBefore + 1
    }

    test("TriggerLifeLossAmount provides correct amount") {
        val driver = createDriver(LifeLossMirror, LoseThreeLife)
        driver.initMirrorMatch(
            deck = Deck.of("Grizzly Bears" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Life Loss Mirror")

        val spell = driver.putCardInHand(activePlayer, "Lose Three Life")
        driver.giveMana(activePlayer, Color.BLACK, 1)
        driver.castSpell(activePlayer, spell).outcome shouldBe Outcome.Done
        driver.bothPass() // resolve Lose Three Life
        driver.bothPass() // resolve triggered ability

        driver.assertLifeTotal(activePlayer, 17) // lost 3
        driver.assertLifeTotal(opponent, 17) // mirrored 3
    }

    test("AnyPlayerLosesLife triggers for either player") {
        val AnyLossWatcher = CardDefinition.enchantment(
            name = "Any Loss Watcher",
            manaCost = ManaCost.parse("{B}"),
            oracleText = "Whenever a player loses life, you draw a card.",
            script = CardScript(
                triggeredAbilities = listOf(
                    TriggeredAbility(
                        id = AbilityId.generate(),
                        trigger = Triggers.AnyPlayerLosesLife.event,
                        binding = Triggers.AnyPlayerLosesLife.binding,
                        effect = DrawCardsEffect(1)
                    )
                )
            )
        )

        val driver = createDriver(AnyLossWatcher, LoseThreeLife)
        driver.initMirrorMatch(
            deck = Deck.of("Grizzly Bears" to 40),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Any Loss Watcher")

        val handSizeBefore = driver.getHand(activePlayer).size

        val spell = driver.putCardInHand(activePlayer, "Lose Three Life")
        driver.giveMana(activePlayer, Color.BLACK, 1)
        driver.castSpell(activePlayer, spell).outcome shouldBe Outcome.Done
        driver.bothPass() // resolve spell
        driver.bothPass() // resolve trigger

        driver.getHand(activePlayer).size shouldBe handSizeBefore + 1
    }
})
