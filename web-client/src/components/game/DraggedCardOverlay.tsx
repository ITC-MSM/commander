import { useEffect, useState } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import { getCardImageUrl } from '@/utils/cardImages.ts'

interface Point {
  x: number
  y: number
}

/**
 * Shows a ghost card following the mouse while dragging from hand.
 */
export function DraggedCardOverlay() {
  const draggingCardId = useGameStore((state) => state.draggingCardId)
  const gameState = useGameStore((state) => state.gameState)
  const [mousePos, setMousePos] = useState<Point | null>(null)

  // Track mouse/touch position while dragging
  useEffect(() => {
    if (!draggingCardId) {
      setMousePos(null)
      return
    }

    // One state update per frame at most; high-rate mice fire mousemove well above 60 Hz.
    let frame: number | null = null
    let latest = { x: 0, y: 0 }
    const schedule = (x: number, y: number) => {
      latest = { x, y }
      if (frame !== null) return
      frame = requestAnimationFrame(() => {
        frame = null
        setMousePos(latest)
      })
    }

    const handleMouseMove = (e: MouseEvent) => {
      schedule(e.clientX, e.clientY)
    }

    const handleTouchMove = (e: TouchEvent) => {
      const touch = e.touches[0]
      if (touch) {
        schedule(touch.clientX, touch.clientY)
      }
    }

    // Set initial position
    setMousePos({ x: 0, y: 0 })

    window.addEventListener('mousemove', handleMouseMove)
    window.addEventListener('touchmove', handleTouchMove)
    return () => {
      if (frame !== null) cancelAnimationFrame(frame)
      window.removeEventListener('mousemove', handleMouseMove)
      window.removeEventListener('touchmove', handleTouchMove)
    }
  }, [draggingCardId])

  if (!draggingCardId || !mousePos || !gameState) return null

  // Get the card info
  const card = gameState.cards[draggingCardId]
  if (!card) return null

  const cardImageUrl = getCardImageUrl(card.name, card.imageUri, 'normal')

  // Card dimensions
  const width = 80
  const height = 112

  return (
    <div
      style={{
        position: 'fixed',
        left: mousePos.x - width / 2,
        top: mousePos.y - height / 2,
        width,
        height,
        pointerEvents: 'none',
        zIndex: 2000,
        opacity: 0.8,
        transform: 'rotate(-5deg) scale(1.1)',
        transition: 'transform 0.1s',
      }}
    >
      <img
        src={cardImageUrl}
        alt={card.name}
        style={{
          width: '100%',
          height: '100%',
          objectFit: 'cover',
          borderRadius: 6,
          boxShadow: '0 8px 24px rgba(0, 0, 0, 0.5), 0 0 16px rgba(0, 221, 255, 0.4)',
        }}
      />
    </div>
  )
}
