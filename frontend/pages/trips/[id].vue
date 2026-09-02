<template>
  <div class="space-y-8">
    <!-- Back Button & Breadcrumbs -->
    <div class="flex items-center gap-3">
      <NuxtLink
        to="/"
        class="px-3.5 py-1.5 rounded-xl bg-slate-900 border border-slate-800 text-xs font-semibold text-slate-300 hover:text-white hover:border-slate-700 transition-colors flex items-center gap-1.5"
      >
        ← Quay lại tìm chuyến
      </NuxtLink>
      <span class="text-xs text-slate-600">/</span>
      <span class="text-xs text-slate-400 font-mono">{{
        trip?.tripCode || id
      }}</span>
    </div>

    <!-- Trip Banner -->
    <div
      v-if="trip"
      class="glass-panel p-6 sm:p-8 rounded-3xl border border-slate-800 flex flex-col md:flex-row items-start md:items-center justify-between gap-6"
    >
      <div>
        <div class="flex items-center gap-2 mb-2">
          <span
            class="text-xs font-bold px-2.5 py-1 rounded-md bg-indigo-500/10 text-indigo-400 border border-indigo-500/20"
          >
            {{ trip.operatorName }}
          </span>
          <span
            class="text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300"
          >
            {{
              trip.vehicleType === "BUS"
                ? "🚌 Xe Khách Giường Nằm"
                : "🚆 Tàu Hỏa Cao Cấp"
            }}
          </span>
        </div>
        <h1 class="text-2xl sm:text-3xl font-extrabold text-white font-display">
          {{ trip.routeName }}
        </h1>
        <p class="text-xs text-slate-400 mt-1">
          Khởi hành:
          <strong class="text-slate-200">{{
            formatDateTime(trip.departureTime)
          }}</strong>
          • Điểm đón:
          <strong class="text-slate-200">{{ trip.departureLocation }}</strong>
        </p>
      </div>

      <div
        class="text-left md:text-right bg-slate-900/60 p-4 rounded-2xl border border-slate-800"
      >
        <div class="text-xs text-slate-400">Đơn giá vé</div>
        <div class="text-2xl font-black text-white gradient-text">
          {{ formatPrice(trip.price) }}
          <span class="text-xs text-slate-400 font-normal">/ ghế</span>
        </div>
      </div>
    </div>

    <!-- Main Content Grid: Seat Map (Left) & Selection Summary (Right) -->
    <div v-if="trip" class="grid grid-cols-1 lg:grid-cols-12 gap-8">
      <!-- Left Column: Interactive Seat Map (8 cols) -->
      <div class="lg:col-span-8">
        <SeatMap :seats="seats" />
      </div>

      <!-- Right Column: Booking Summary & Hold Action (4 cols) -->
      <div class="lg:col-span-4 space-y-6">
        <div
          class="glass-panel p-6 rounded-2xl border border-slate-800 space-y-6 sticky top-24"
        >
          <h3
            class="text-lg font-bold text-white border-b border-slate-800/80 pb-3 flex items-center justify-between"
          >
            <span>Thông Tin Đặt Chỗ</span>
            <span
              class="text-xs font-mono px-2 py-0.5 rounded bg-indigo-500/20 text-indigo-300"
              >Tối đa 4 ghế</span
            >
          </h3>

          <!-- Selected Seats Display -->
          <div>
            <div class="text-xs font-semibold text-slate-400 mb-2">
              Ghế đang chọn:
            </div>
            <div
              v-if="bookingStore.selectedSeatNumbers.length > 0"
              class="flex flex-wrap gap-2"
            >
              <span
                v-for="seat in bookingStore.selectedSeatNumbers"
                :key="seat"
                class="px-3 py-1.5 rounded-xl bg-blue-600/20 border border-blue-500/40 text-blue-300 font-bold text-sm flex items-center gap-1.5"
              >
                Ghế {{ seat }}
                <button
                  @click="bookingStore.toggleSeatSelection(seat)"
                  class="text-blue-400 hover:text-white text-xs"
                >
                  ✕
                </button>
              </span>
            </div>
            <div
              v-else
              class="text-xs text-slate-500 italic bg-slate-900/50 p-3 rounded-xl border border-slate-800/60"
            >
              Vui lòng bấm vào sơ đồ bên cạnh để chọn vị trí ghế mong muốn.
            </div>
          </div>

          <!-- Customer Form -->
          <div class="space-y-3 pt-2">
            <div>
              <label
                for="customerName"
                class="block text-xs text-slate-400 mb-1"
                >Họ tên hành khách</label
              >
              <input
                v-model="customerName"
                type="text"
                id="customerName"
                placeholder="Nguyễn Văn A"
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2 text-xs text-white placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label
                for="customerPhone"
                class="block text-xs text-slate-400 mb-1"
                >Số điện thoại</label
              >
              <input
                v-model="customerPhone"
                type="tel"
                id="customerPhone"
                placeholder="0912345678"
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2 text-xs text-white placeholder:text-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <!-- Price Calculation -->
          <div class="pt-4 border-t border-slate-800/80 space-y-2">
            <div class="flex justify-between text-xs text-slate-400">
              <span>Số lượng ghế:</span>
              <strong class="text-white"
                >{{ bookingStore.selectedSeatNumbers.length }} ghế</strong
              >
            </div>
            <div class="flex justify-between text-sm text-slate-300 pt-1">
              <span class="font-bold">Tổng thanh toán:</span>
              <span class="text-xl font-black gradient-text">{{
                formatPrice(bookingStore.totalSelectedAmount)
              }}</span>
            </div>
          </div>

          <!-- Hold Seats Action Button -->
          <button
            :disabled="
              bookingStore.selectedSeatNumbers.length === 0 || isHolding
            "
            class="w-full py-3 px-6 rounded-xl font-bold text-sm text-white bg-gradient-to-r from-indigo-600 to-sky-600 hover:from-indigo-500 hover:to-sky-500 disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-lg shadow-indigo-600/25 flex items-center justify-center gap-2"
            @click="handleHoldSeats"
          >
            <span v-if="isHolding">Đang xử lý khóa ghế (Redlock)...</span>
            <span v-else>🔒 Giữ Chỗ & Thanh Toán (5 Phút)</span>
          </button>

          <!-- Warning Notice -->
          <div
            class="bg-amber-500/10 border border-amber-500/20 p-3 rounded-xl text-[11px] text-amber-300/90 leading-relaxed flex items-start gap-2"
          >
            <span>⏱️</span>
            <span
              >Ghế sẽ được khóa giữ chỗ tạm thời trong đúng
              <strong>5 phút</strong>. Quá thời gian này, hệ thống sẽ tự động
              nhả ghế nếu chưa hoàn tất thanh toán.</span
            >
          </div>
        </div>
      </div>
    </div>

    <!-- Error/Loading state if trip not found -->
    <div v-else class="glass-panel p-12 rounded-3xl text-center space-y-4">
      <div class="text-3xl">⚠️</div>
      <h3 class="text-lg font-bold text-white">
        Đang tải hoặc không tìm thấy thông tin chuyến đi
      </h3>
      <p class="text-xs text-slate-400">
        Vui lòng kiểm tra lại đường dẫn hoặc kết nối API Gateway.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useBookingStore, type Trip, type TripSeat } from "~/stores/booking";
import { useSeatWebSocket } from "~/composables/useWebSocket";

const route = useRoute();
const router = useRouter();
const config = useRuntimeConfig();
const bookingStore = useBookingStore();

const id = route.params.id as string;
const trip = ref<Trip | null>(null);
const seats = ref<TripSeat[]>([]);
const isHolding = ref(false);

const customerName = ref("");
const customerPhone = ref("");

// Khởi tạo WebSocket listener nhận broadcast thay đổi trạng thái ghế
useSeatWebSocket(id, (updateMessage: any) => {
  console.log("[WebSocket Broadcast Received]:", updateMessage);
  if (updateMessage && updateMessage.seatNumbers) {
    updateMessage.seatNumbers.forEach((seatNum: string) => {
      const targetSeat = seats.value.find((s: any) => s.seatNumber === seatNum);
      if (targetSeat) {
        targetSeat.status = updateMessage.status;
        targetSeat.heldByUserId = updateMessage.userId;

        // Nếu ghế bạn đang chọn vừa bị người khác giữ trước -> bỏ chọn
        if (
          updateMessage.status === "HELD" &&
          updateMessage.userId !== bookingStore.userId
        ) {
          const idx = bookingStore.selectedSeatNumbers.indexOf(seatNum);
          if (idx >= 0) {
            bookingStore.selectedSeatNumbers.splice(idx, 1);
          }
        }
      }
    });
  }
});

const fetchTripDetails = async () => {
  try {
    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/trips/${id}`,
    );
    if (res && res.success && res.data) {
      trip.value = res.data;
      bookingStore.setCurrentTrip(res.data);
    }
  } catch (error) {
    console.error("Lỗi khi lấy chi tiết chuyến đi:", error);
    trip.value = null;
  }
};

const fetchSeats = async () => {
  try {
    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/trips/${id}/seats`,
    );
    if (res && res.success && res.data) {
      seats.value = res.data;
    } else {
      seats.value = [];
    }
  } catch (error) {
    console.error("Lỗi khi lấy sơ đồ ghế từ API:", error);
    seats.value = [];
  }
};

const handleHoldSeats = async () => {
  if (bookingStore.selectedSeatNumbers.length === 0) return;
  isHolding.value = true;

  try {
    const payload = {
      tripId: id,
      userId: bookingStore.userId,
      customerName: customerName.value,
      customerPhone: customerPhone.value,
      seatNumbers: bookingStore.selectedSeatNumbers,
    };

    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/bookings/hold`,
      {
        method: "POST",
        headers: {
          "X-User-Id": bookingStore.userId,
        },
        body: payload,
      },
    );

    if (res && res.success && res.data) {
      bookingStore.setHeldBooking(res.data);
      router.push("/checkout");
    }
  } catch (error: any) {
    console.error("Lỗi khi giữ ghế:", error);
    const errorMsg =
      error?.data?.message ||
      "Không thể giữ ghế do có người khác đang giữ hoặc lỗi kết nối. Vui lòng thử lại!";
    alert(errorMsg);
    // Tải lại sơ đồ ghế thực tế từ server
    fetchSeats();
  } finally {
    isHolding.value = false;
  }
};

const formatDateTime = (isoString: string) => {
  if (!isoString) return "";
  const date = new Date(isoString);
  return date.toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const formatPrice = (amount: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

onMounted(() => {
  fetchTripDetails();
  fetchSeats();
});
</script>
