<template>
  <div class="glass-panel p-6 rounded-2xl border border-slate-800">
    <!-- Header & Deck Selector -->
    <div
      class="flex flex-col sm:flex-row items-center justify-between gap-4 pb-6 border-b border-slate-800/80"
    >
      <div>
        <h3 class="text-lg font-bold text-white flex items-center gap-2">
          <span>💺 Sơ Đồ Chỗ Ngồi Trực Quan</span>
        </h3>
        <p class="text-xs text-slate-400 mt-1">
          Chọn ghế để giữ chỗ trong 5 phút. Tối đa 4 ghế/lượt.
        </p>
      </div>

      <!-- Deck Switcher (Tầng 1 / Tầng 2) -->
      <div
        class="flex items-center bg-slate-900/90 p-1 rounded-xl border border-slate-800"
      >
        <button
          :class="[
            'px-4 py-1.5 rounded-lg text-xs font-semibold transition-all',
            activeDeck === 1
              ? 'bg-indigo-600 text-white shadow-md'
              : 'text-slate-400 hover:text-white',
          ]"
          @click="activeDeck = 1"
        >
          Tầng Dưới
        </button>
        <button
          :class="[
            'px-4 py-1.5 rounded-lg text-xs font-semibold transition-all',
            activeDeck === 2
              ? 'bg-indigo-600 text-white shadow-md'
              : 'text-slate-400 hover:text-white',
          ]"
          @click="activeDeck = 2"
        >
          Tầng Trên
        </button>
      </div>
    </div>

    <!-- Seat Legend (Chú thích) -->
    <div
      class="flex flex-wrap items-center justify-center gap-6 py-4 border-b border-slate-800/60 text-xs text-slate-300"
    >
      <div class="flex items-center gap-2">
        <div
          class="w-5 h-5 rounded-md border border-emerald-500/60 bg-slate-900 flex items-center justify-center text-[10px] text-emerald-400"
        >
          ✓
        </div>
        <span>Ghế trống</span>
      </div>
      <div class="flex items-center gap-2">
        <div
          class="w-5 h-5 rounded-md bg-blue-600 text-white font-bold flex items-center justify-center text-[10px] ring-2 ring-blue-400"
        >
          ✓
        </div>
        <span>Đang chọn</span>
      </div>
      <div class="flex items-center gap-2">
        <div
          class="w-5 h-5 rounded-md border border-amber-500 bg-amber-500/20 text-amber-300 flex items-center justify-center text-[10px] animate-pulse"
        >
          🔒
        </div>
        <span>Đang giữ chỗ</span>
      </div>
      <div class="flex items-center gap-2">
        <div
          class="w-5 h-5 rounded-md bg-slate-800/80 border border-slate-700/50 text-slate-500 flex items-center justify-center text-[10px]"
        >
          ✕
        </div>
        <span>Đã bán</span>
      </div>
    </div>

    <!-- Vehicle Bus Layout Preview -->
    <div class="mt-8 flex justify-center">
      <div
        class="w-full max-w-md bg-slate-900/50 border border-slate-800 rounded-3xl p-6 shadow-2xl relative"
      >
        <!-- Driver Cabin Area -->
        <div
          class="flex items-center justify-between pb-4 mb-6 border-b border-slate-800/80 text-xs text-slate-500"
        >
          <div
            class="flex items-center gap-1.5 bg-slate-800/60 px-3 py-1 rounded-full"
          >
            <span>🚪 Cửa lên xuống</span>
          </div>
          <div
            class="flex items-center gap-1.5 bg-slate-800/60 px-3 py-1 rounded-full text-slate-400"
          >
            <span>👨‍✈️ Bác tài</span>
          </div>
        </div>

        <!-- Seat Grid (3 cols layout) -->
        <div class="grid grid-cols-3 gap-y-4 gap-x-6 justify-items-center">
          <div
            v-for="seat in filteredSeats"
            :key="seat.id"
            class="w-12 h-14 rounded-xl border flex flex-col items-center justify-between p-1.5 transition-all duration-200 select-none group"
            :class="getSeatClasses(seat)"
            @click="handleSeatClick(seat)"
          >
            <div class="text-[9px] font-bold uppercase tracking-wider">
              {{ seat.seatType === "VIP" ? "VIP" : "" }}
            </div>

            <!-- Seat Number -->
            <div class="font-bold text-xs tracking-tight">
              {{ seat.seatNumber }}
            </div>

            <!-- Seat Bottom Indicator / Status icon -->
            <div
              class="w-6 h-1 rounded-full opacity-75"
              :class="getSeatBottomBarColor(seat)"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useBookingStore, type TripSeat } from "~/stores/booking";

const props = defineProps<{
  seats: TripSeat[];
}>();

const emit = defineEmits<{
  (e: "select", seat: TripSeat): void;
}>();

const bookingStore = useBookingStore();
const activeDeck = ref(1);

const filteredSeats = computed(() => {
  return props.seats.filter((s) => s.deck === activeDeck.value);
});

const isSeatSelected = (seatNumber: string) => {
  return bookingStore.selectedSeatNumbers.includes(seatNumber);
};

const handleSeatClick = (seat: TripSeat) => {
  if (seat.status === "BOOKED" || seat.status === "BLOCKED") return;
  if (seat.status === "HELD" && seat.heldByUserId !== bookingStore.userId) {
    alert("Ghế này đang được người khác giữ chỗ trong 5 phút!");
    return;
  }
  bookingStore.toggleSeatSelection(seat.seatNumber);
  emit("select", seat);
};

const getSeatClasses = (seat: TripSeat) => {
  // 1. If currently selected by this user
  if (isSeatSelected(seat.seatNumber)) {
    return "border-blue-500 bg-blue-600 text-white shadow-lg shadow-blue-500/30 scale-105 cursor-pointer ring-2 ring-blue-400";
  }
  // 2. If held by others
  if (seat.status === "HELD") {
    return "border-amber-500/80 bg-amber-500/20 text-amber-300 animate-pulse cursor-not-allowed";
  }
  // 3. If booked
  if (seat.status === "BOOKED" || seat.status === "BLOCKED") {
    return "border-slate-800 bg-slate-900/60 text-slate-600 cursor-not-allowed opacity-50";
  }
  // 4. Available
  return "border-emerald-500/40 bg-slate-900/80 text-emerald-300 hover:border-emerald-400 hover:bg-emerald-500/10 hover:scale-105 cursor-pointer shadow-sm shadow-emerald-500/5";
};

const getSeatBottomBarColor = (seat: TripSeat) => {
  if (isSeatSelected(seat.seatNumber)) return "bg-white";
  if (seat.status === "HELD") return "bg-amber-400";
  if (seat.status === "BOOKED") return "bg-slate-700";
  return "bg-emerald-400";
};
</script>
