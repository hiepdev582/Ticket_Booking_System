import { defineStore } from 'pinia'

export interface TripSeat {
  id: string
  tripId: string
  seatNumber: string
  deck: number
  seatType: string
  status: 'AVAILABLE' | 'HELD' | 'BOOKED' | 'BLOCKED'
  price: number
  heldByUserId?: string
}

export interface Trip {
  id: string
  tripCode: string
  operatorName: string
  routeName: string
  departureLocation: string
  arrivalLocation: string
  departureTime: string
  arrivalTime: string
  vehicleType: 'BUS' | 'TRAIN'
  totalSeats: number
  availableSeats: number
  price: number
}

export interface HeldBooking {
  bookingId: string
  bookingCode: string
  tripId: string
  userId: string
  seatNumbers: string[]
  totalAmount: number
  status: string
  expiresAt: string
  remainingSeconds: number
  customerName?: string
  customerPhone?: string
  customerEmail?: string
}

export const useBookingStore = defineStore('booking', {
  state: () => ({
    userId: 'user-' + Math.random().toString(36).substring(2, 9),
    currentTrip: null as Trip | null,
    selectedSeatNumbers: [] as string[],
    heldBooking: null as HeldBooking | null,
    countdownSeconds: 0,
    timerInterval: null as any,
  }),

  getters: {
    totalSelectedAmount(state): number {
      if (!state.currentTrip) return 0
      return state.selectedSeatNumbers.length * state.currentTrip.price
    },
    formattedTimeLeft(state): string {
      const minutes = Math.floor(state.countdownSeconds / 60)
      const seconds = state.countdownSeconds % 60
      return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
    },
    isExpired(state): boolean {
      return state.heldBooking !== null && state.countdownSeconds <= 0
    }
  },

  actions: {
    setCurrentTrip(trip: Trip) {
      this.currentTrip = trip
      this.selectedSeatNumbers = []
    },

    toggleSeatSelection(seatNumber: string) {
      const index = this.selectedSeatNumbers.indexOf(seatNumber)
      if (index >= 0) {
        this.selectedSeatNumbers.splice(index, 1)
      } else {
        if (this.selectedSeatNumbers.length >= 4) {
          alert('Bạn chỉ có thể chọn tối đa 4 ghế trong một lượt đặt!')
          return
        }
        this.selectedSeatNumbers.push(seatNumber)
      }
    },

    setHeldBooking(booking: HeldBooking) {
      this.heldBooking = booking
      this.countdownSeconds = booking.remainingSeconds || 300
      this.startCountdown()
    },

    startCountdown() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval)
      }
      this.timerInterval = setInterval(() => {
        if (this.countdownSeconds > 0) {
          this.countdownSeconds--
        } else {
          this.stopCountdown()
        }
      }, 1000)
    },

    stopCountdown() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval)
        this.timerInterval = null
      }
    },

    clearHeldBooking() {
      this.stopCountdown()
      this.heldBooking = null
      this.selectedSeatNumbers = []
      this.countdownSeconds = 0
    }
  }
})
