package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SaheeliTheSunsBrilliance
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Saheeli, the Sun's Brilliance (LCI #239) — {U}{R} 2/2 Legendary Creature — Human Artificer.
 *
 * "{U}{R}, {T}: Create a token that's a copy of another target creature or artifact you control,
 *  except it's an artifact in addition to its other types. It gains haste. Sacrifice it at the
 *  beginning of the next end step."
 *
 * Mirrors Molten Duplication / The Jolly Balloon Man: [Effects.CreateTokenCopyOfTarget] with
 * `addCardTypes = ARTIFACT` (adds the artifact type on top of the copy's own types),
 * `addedKeywords = HASTE`, and `sacrificeAtStep = END` (delayed sacrifice at the next end step).
 * The target is "another" (TargetOther excludes Saheeli herself) and may be a creature OR an
 * artifact you control. No sorcery-speed restriction.
 */
class SaheeliTheSunsBrillianceScenarioTest : FunSpec({

    val abilityId = SaheeliTheSunsBrilliance.activatedAbilities.first().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun newTokenOf(driver: GameTestDriver, existing: List<EntityId>): EntityId =
        driver.state.getBattlefield(driver.player1).first {
            it !in existing &&
                driver.state.getEntity(it)?.has<TokenComponent>() == true
        }

    fun activate(driver: GameTestDriver, saheeli: EntityId, target: EntityId) {
        // {U}{R} needs a blue and a red source — give Saheeli's controller one of each.
        driver.putPermanentOnBattlefield(driver.player1, "Island")
        driver.putPermanentOnBattlefield(driver.player1, "Mountain")
        val result = driver.submit(
            ActivateAbility(
                playerId = driver.player1,
                sourceId = saheeli,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(target)),
                costPayment = null,
            )
        )
        result.error shouldBe null
        driver.bothPass()
    }

    test("copying a creature: token is that creature plus artifact, with haste") {
        val driver = newDriver()
        val saheeli = driver.putCreatureOnBattlefield(driver.player1, "Saheeli, the Sun's Brilliance")
        driver.removeSummoningSickness(saheeli)
        val bears = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        activate(driver, saheeli, bears)

        val token = newTokenOf(driver, listOf(saheeli, bears))
        val projected = driver.state.projectedState
        projected.isCreature(token) shouldBe true
        projected.hasType(token, "ARTIFACT") shouldBe true
        projected.getPower(token) shouldBe 2
        projected.getToughness(token) shouldBe 2
        projected.hasKeyword(token, Keyword.HASTE) shouldBe true
    }

    test("copying an artifact: token is a copy of that artifact and is an artifact") {
        val driver = newDriver()
        val saheeli = driver.putCreatureOnBattlefield(driver.player1, "Saheeli, the Sun's Brilliance")
        driver.removeSummoningSickness(saheeli)
        val mindStone = driver.putPermanentOnBattlefield(driver.player1, "Mind Stone")

        activate(driver, saheeli, mindStone)

        val token = newTokenOf(driver, listOf(saheeli, mindStone))
        val projected = driver.state.projectedState
        projected.hasType(token, "ARTIFACT") shouldBe true
        projected.hasKeyword(token, Keyword.HASTE) shouldBe true
    }

    test("the token copy is sacrificed at the next end step") {
        val driver = newDriver()
        val saheeli = driver.putCreatureOnBattlefield(driver.player1, "Saheeli, the Sun's Brilliance")
        driver.removeSummoningSickness(saheeli)
        val bears = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        activate(driver, saheeli, bears)
        val token = newTokenOf(driver, listOf(saheeli, bears))

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.state.getBattlefield(driver.player1).contains(token) shouldBe false
    }
})
