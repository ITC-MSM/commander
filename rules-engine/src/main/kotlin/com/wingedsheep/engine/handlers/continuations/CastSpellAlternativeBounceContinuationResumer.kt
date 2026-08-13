package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.CastSpellAlternativeBounceContinuation
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler
import com.wingedsheep.engine.state.GameState

/** Continues Sneak/Web-slinging after the Commander replacement has performed its physical move. */
class CastSpellAlternativeBounceContinuationResumer(services: EngineServices) : AutoResumerModule {
    private val castSpellHandler: CastSpellHandler by lazy { CastSpellHandler.create(services) }

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(CastSpellAlternativeBounceContinuation::class) { state, continuation, events, _ ->
            castSpellHandler.resumeAfterAlternativeBounce(state, continuation, events)
        }
    )
}
