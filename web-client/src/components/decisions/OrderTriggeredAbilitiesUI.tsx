import { useState } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import type { OrderTriggeredAbilitiesDecision } from '@/types'

/** Deliberately text-first: trigger instances need not be cards and may share a source. */
export function OrderTriggeredAbilitiesUI({ decision }: { decision: OrderTriggeredAbilitiesDecision }) {
  const [items, setItems] = useState([...decision.abilities])
  const submit = useGameStore((state) => state.submitTriggeredAbilitiesOrder)
  const move = (from: number, to: number) => setItems((current) => {
    const next = [...current]
    const [item] = next.splice(from, 1) as [typeof next[number]]
    next.splice(to, 0, item)
    return next
  })
  return <div style={{ maxWidth: 640, width: 'calc(100% - 32px)', background: '#18212f', color: 'white', borderRadius: 12, padding: 24 }}>
    <h2 style={{ marginTop: 0 }}>Order triggered abilities</h2>
    <p>The first ability listed is put on the stack first and resolves last.</p>
    {items.map((item, index) => <div key={item.id} style={{ display: 'flex', gap: 10, alignItems: 'center', padding: '10px 0', borderTop: '1px solid #334155' }}>
      <div style={{ flex: 1 }}><strong>{item.sourceName}</strong><br /><span>{item.description}</span></div>
      <button disabled={index === 0} onClick={() => move(index, index - 1)}>↑</button>
      <button disabled={index === items.length - 1} onClick={() => move(index, index + 1)}>↓</button>
    </div>)}
    <button style={{ marginTop: 18 }} onClick={() => submit(items.map((item) => item.id))}>Confirm order</button>
  </div>
}
