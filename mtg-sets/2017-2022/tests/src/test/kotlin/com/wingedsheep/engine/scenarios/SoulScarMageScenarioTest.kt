package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Soul-Scar Mage (AKH) — "If a source you control would deal noncombat damage to a creature an
 * opponent controls, put that many -1/-1 counters on that creature instead."
 *
 * A `ReplaceDamageWithCounters` whose event pattern carries all three clauses
 * (`source = YouControl`, `damageType = NonCombat`, `recipient = CreatureOpponentControls`) and
 * whose counters land on the *damaged* permanent rather than on the Mage
 * (`DamageCounterRecipient.DamagedPermanent`).
 *
 * The tests below pin each clause separately, because each one is a way the replacement could
 * over-apply: combat damage, damage to your own creatures, damage to players, and damage from a
 * source your opponent controls all have to stay ordinary damage.
 */
class SoulScarMageScenarioTest : FunSpec({

    fun newGame(): Triple<GameTestDriver, EntityId, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }
        return Triple(driver, you, opponent)
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    /**
     * Cast a Lightning Bolt from [caster] at [target] and let it resolve. The cast is asserted, not
     * assumed — a bolt that never got cast would otherwise read as "the replacement did not apply".
     */
    fun GameTestDriver.bolt(caster: EntityId, target: ChosenTarget) {
        giveMana(caster, Color.RED, 1)
        val bolt = putCardInHand(caster, "Lightning Bolt")
        // The non-active player only gets to cast an instant once the active player passes.
        if (priorityPlayer != caster) passPriority(priorityPlayer!!)
        castSpellWithTargets(caster, bolt, listOf(target)).error shouldBe null
        bothPass()
        resolveStack(this)
    }

    fun GameTestDriver.counters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.MINUS_ONE_MINUS_ONE) ?: 0

    fun GameTestDriver.markedDamage(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("noncombat damage from a spell you control becomes -1/-1 counters on the opponent's creature") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val wurm = driver.putCreatureOnBattlefield(opponent, "Craw Wurm") // 6/4

        driver.bolt(you, ChosenTarget.Permanent(wurm)) // 3 damage

        // Replaced outright (CR 614.1a): no damage is marked, three -1/-1 counters instead, and the
        // shrink is permanent — a 6/4 becomes a 3/1 rather than a 6/4 with 3 damage on it.
        driver.markedDamage(wurm) shouldBe 0
        driver.counters(wurm) shouldBe 3
        driver.state.projectedState.getPower(wurm) shouldBe 3
        driver.state.projectedState.getToughness(wurm) shouldBe 1
        driver.state.getBattlefield().contains(wurm) shouldBe true
    }

    test("enough -1/-1 counters kill the creature as a state-based action") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser") // 3/3

        driver.bolt(you, ChosenTarget.Permanent(courser)) // 3 damage -> three -1/-1 counters -> 0/0

        driver.state.getBattlefield().contains(courser) shouldBe false
    }

    test("combat damage is untouched — the clause is noncombat only") {
        val (driver, you, opponent) = newGame()
        val mage = driver.putCreatureOnBattlefield(you, "Soul-Scar Mage") // 1/2
        driver.removeSummoningSickness(mage)
        val wurm = driver.putCreatureOnBattlefield(opponent, "Craw Wurm") // 6/4

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(mage), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(wurm to listOf(mage)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        resolveStack(driver)

        // The Mage's own 1 combat damage is marked on the Wurm as normal damage, not converted.
        driver.counters(wurm) shouldBe 0
        driver.markedDamage(wurm) shouldBe 1
    }

    test("damage to a creature you control is untouched") {
        val (driver, you, _) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val yourWurm = driver.putCreatureOnBattlefield(you, "Craw Wurm") // 6/4

        driver.bolt(you, ChosenTarget.Permanent(yourWurm))

        driver.counters(yourWurm) shouldBe 0
        driver.markedDamage(yourWurm) shouldBe 3
    }

    test("damage to a player is untouched") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")

        driver.bolt(you, ChosenTarget.Player(opponent))

        driver.getLifeTotal(opponent) shouldBe 17
    }

    test("damage from a source your opponent controls is untouched") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val theirWurm = driver.putCreatureOnBattlefield(opponent, "Craw Wurm") // 6/4

        // The opponent bolts their own creature. The recipient matches ("a creature an opponent
        // controls", from the Mage's controller's point of view) but the *source* is not one you
        // control, so the replacement does not apply.
        driver.bolt(opponent, ChosenTarget.Permanent(theirWurm))

        driver.counters(theirWurm) shouldBe 0
        driver.markedDamage(theirWurm) shouldBe 3
    }

    test("fight damage is noncombat damage — the opponent's creature takes counters, yours takes damage") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val yourCourser = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        val theirWurm = driver.putCreatureOnBattlefield(opponent, "Craw Wurm") // 6/4

        driver.giveMana(you, Color.GREEN, 1)
        driver.giveColorlessMana(you, 1)
        val punch = driver.putCardInHand(you, "Savage Punch")
        driver.castSpellWithTargets(
            you, punch,
            listOf(ChosenTarget.Permanent(yourCourser), ChosenTarget.Permanent(theirWurm))
        )
        driver.bothPass()
        resolveStack(driver)

        // 2017-04-18 ruling: the damage is simultaneous, and only the half aimed at the opponent's
        // creature is replaced. Your Courser is dealt 6 and dies; their Wurm gets three -1/-1
        // counters instead of 3 damage.
        driver.counters(theirWurm) shouldBe 3
        driver.markedDamage(theirWurm) shouldBe 0
        driver.state.getBattlefield().contains(yourCourser) shouldBe false
    }

    /**
     * Activate [cardName]'s first activated ability from [controller] at [target] and let it
     * resolve. Everything the Mage cares about is on the source side, so the two cases below differ
     * only in whether the source is still on the battlefield when the damage is dealt.
     */
    fun GameTestDriver.ping(controller: EntityId, cardName: String, source: EntityId, target: ChosenTarget) {
        val abilityId = cardRegistry.getCard(cardName)!!.activatedAbilities.first().id
        submit(
            ActivateAbility(
                playerId = controller,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(target)
            )
        ).error shouldBe null
        bothPass()
        resolveStack(this)
    }

    test("noncombat damage from an activated ability you control becomes -1/-1 counters") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        val sorcerer = driver.putCreatureOnBattlefield(you, "Prodigal Sorcerer")
        driver.removeSummoningSickness(sorcerer)
        val angel = driver.putCreatureOnBattlefield(opponent, "Serra Angel") // 4/4

        // The clause covers any source you control, not just spells — an ability's source counts.
        driver.ping(you, "Prodigal Sorcerer", sorcerer, ChosenTarget.Permanent(angel))

        driver.counters(angel) shouldBe 1
        driver.markedDamage(angel) shouldBe 0
    }

    test("a source sacrificed to pay its own cost still counts as a source you control") {
        val (driver, you, opponent) = newGame()
        driver.putCreatureOnBattlefield(you, "Soul-Scar Mage")
        // "{T}, Sacrifice this creature: It deals 1 damage to any target" — haste, so no need to
        // clear summoning sickness for the tap.
        val firebrand = driver.putCreatureOnBattlefield(you, "Fanatical Firebrand")
        val angel = driver.putCreatureOnBattlefield(opponent, "Serra Angel") // 4/4

        driver.ping(you, "Fanatical Firebrand", firebrand, ChosenTarget.Permanent(angel))

        // The Firebrand is in the graveyard by the time it deals the damage, so its controller has
        // to come from last-known information (CR 113.7a / 608.2h). Reading the live state instead
        // found no controller at all and the replacement silently declined.
        driver.state.getBattlefield().contains(firebrand) shouldBe false
        driver.counters(angel) shouldBe 1
        driver.markedDamage(angel) shouldBe 0
    }

    test("prowess still works") {
        val (driver, you, opponent) = newGame()
        val mage = driver.putCreatureOnBattlefield(you, "Soul-Scar Mage") // 1/2
        val wurm = driver.putCreatureOnBattlefield(opponent, "Craw Wurm")

        driver.bolt(you, ChosenTarget.Permanent(wurm))

        driver.state.projectedState.getPower(mage) shouldBe 2
        driver.state.projectedState.getToughness(mage) shouldBe 3
    }
})
