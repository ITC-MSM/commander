package com.wingedsheep.gym.server.config

import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

/**
 * Guards the gym's card registry against the registration drift that made self-play lie.
 *
 * `CreatePredefinedTokenExecutor` resolves a token by *name* out of the `CardRegistry` and returns
 * an `EffectResult.error` when the name is missing — no exception, no server-side log. The gym's
 * registry was built from set cards and basic lands only, so every Treasure / Food / Clue / Map /
 * Incubator card in the corpus minted nothing over the gym API while behaving correctly in
 * `game-server`, whose `GameBeansConfig` does register them. Self-play duly reported those cards as
 * broken cards rather than as a broken harness.
 */
class GymBeansConfigTest : FunSpec({

    test("the gym card registry resolves every predefined token by name") {
        val registry = GymBeansConfig().cardRegistry()

        for (token in PredefinedTokens.allTokens) {
            withClue("predefined token '${token.name}' must be registered for the gym") {
                registry.getCard(token.name) shouldNotBe null
            }
        }
    }
})
