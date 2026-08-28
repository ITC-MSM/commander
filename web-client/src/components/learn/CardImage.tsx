import { useEffect, useState } from 'react'
import { createPortal } from 'react-dom'
import styles from './learn.module.css'

/**
 * A real card, from the catalog's image (or Scryfall's), with an optional caption and note
 * under it. Hovering lifts it; clicking opens it full-size — the same "read the card" the table
 * offers, so the gesture is learned before the game starts.
 */
export function CardImage({
  src,
  name,
  caption,
  note,
}: {
  src: string
  name: string
  caption?: string
  note?: string
}) {
  const [open, setOpen] = useState(false)

  useEffect(() => {
    if (!open) return
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setOpen(false)
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [open])

  return (
    <div className={styles.cardTile}>
      <button
        type="button"
        className={styles.cardButton}
        onClick={() => setOpen(true)}
        aria-label={`Read ${name} in full`}
      >
        <img src={src} alt={name} className={styles.cardImg} loading="lazy" draggable={false} />
      </button>
      {caption !== undefined && <div className={styles.cardName}>{caption}</div>}
      {note !== undefined && <p className={styles.cardNote}>{note}</p>}
      {open &&
        createPortal(
          <div className={styles.lightbox} onClick={() => setOpen(false)} role="dialog" aria-label={name}>
            <img src={src} alt={name} className={styles.lightboxImg} draggable={false} />
            {note !== undefined && <p className={styles.lightboxNote}>{note}</p>}
            <p className={styles.lightboxHint}>Click anywhere to close</p>
          </div>,
          document.body,
        )}
    </div>
  )
}
