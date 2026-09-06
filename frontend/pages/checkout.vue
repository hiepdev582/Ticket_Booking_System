<template>
  <div class="max-w-3xl mx-auto space-y-8">
    <!-- Active Hold Countdown Banner -->
    <div
      v-if="bookingStore.heldBooking"
      class="glass-panel p-6 rounded-3xl border border-amber-500/30 bg-amber-500/5 shadow-2xl relative overflow-hidden"
    >
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4">
        <div class="flex items-center gap-4">
          <div
            class="w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-500/40 flex items-center justify-center text-2xl font-bold text-amber-300 animate-pulse"
          >
            ⏱️
          </div>
          <div>
            <div
              class="text-xs font-semibold text-amber-400/80 uppercase tracking-widest"
            >
              Thời Gian Giữ Chỗ Còn Lại
            </div>
            <div
              class="text-3xl sm:text-4xl font-black font-display text-white mt-0.5"
            >
              {{ bookingStore.formattedTimeLeft }}
            </div>
          </div>
        </div>

        <div class="text-xs text-slate-400 text-center sm:text-right max-w-xs">
          Vui lòng hoàn tất thanh toán trước khi thời gian đếm ngược kết thúc để
          tránh bị hủy ghế.
        </div>
      </div>

      <!-- Time Progress Bar -->
      <div
        class="w-full bg-slate-900/80 h-1.5 rounded-full mt-4 overflow-hidden"
      >
        <div
          class="bg-gradient-to-r from-amber-500 to-red-500 h-full transition-all duration-1000"
          :style="{ width: `${(bookingStore.countdownSeconds / 300) * 100}%` }"
        ></div>
      </div>
    </div>

    <!-- Booking Order Summary Card -->
    <div
      v-if="bookingStore.heldBooking"
      class="glass-panel p-6 sm:p-8 rounded-3xl border border-slate-800 space-y-6"
    >
      <h2
        class="text-xl font-bold text-white font-display border-b border-slate-800/80 pb-4"
      >
        Chi Tiết Đơn Đặt Vé #{{ bookingStore.heldBooking.bookingCode }}
      </h2>

      <div class="space-y-4 text-sm">
        <div class="flex justify-between py-2 border-b border-slate-800/50">
          <span class="text-slate-400">Tuyến xe:</span>
          <strong class="text-white">{{
            bookingStore.currentTrip?.routeName || "Chi tiết chuyến"
          }}</strong>
        </div>
        <div class="flex justify-between py-2 border-b border-slate-800/50">
          <span class="text-slate-400">Nhà xe:</span>
          <span class="text-indigo-400 font-semibold">{{
            bookingStore.currentTrip?.operatorName || "Nhà xe"
          }}</span>
        </div>
        <div class="flex justify-between py-2 border-b border-slate-800/50">
          <span class="text-slate-400">Vị trí ghế đã chọn:</span>
          <div class="flex gap-1.5">
            <span
              v-for="seat in bookingStore.heldBooking.seatNumbers"
              :key="seat"
              class="px-2.5 py-0.5 rounded-lg bg-blue-600/20 text-blue-300 font-bold text-xs border border-blue-500/30"
            >
              Ghế {{ seat }}
            </span>
          </div>
        </div>
        <div class="flex justify-between py-2 border-b border-slate-800/50">
          <span class="text-slate-400">Tổng tiền thanh toán:</span>
          <span class="text-2xl font-black gradient-text">
            {{ formatPrice(bookingStore.heldBooking.totalAmount) }}
          </span>
        </div>
      </div>

      <!-- Payment Method Selection -->
      <div class="space-y-4 pt-4">
        <h3 class="text-sm font-bold text-slate-300">Phương Thức Thanh Toán</h3>

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <label
            :class="[
              'p-4 rounded-2xl border flex flex-col items-center justify-center gap-2 cursor-pointer transition-all',
              selectedMethod === 'VNPAY'
                ? 'border-indigo-500 bg-indigo-500/10 ring-2 ring-indigo-500/30'
                : 'border-slate-800 bg-slate-900/60 hover:border-slate-700',
            ]"
          >
            <input
              v-model="selectedMethod"
              type="radio"
              value="VNPAY"
              class="sr-only"
            />
            <span class="text-2xl">🏦</span>
            <span class="font-bold text-xs text-white">VNPay QR</span>
          </label>

          <label
            :class="[
              'p-4 rounded-2xl border flex flex-col items-center justify-center gap-2 cursor-pointer transition-all',
              selectedMethod === 'MOMO'
                ? 'border-pink-500 bg-pink-500/10 ring-2 ring-pink-500/30'
                : 'border-slate-800 bg-slate-900/60 hover:border-slate-700',
            ]"
          >
            <input
              v-model="selectedMethod"
              type="radio"
              value="MOMO"
              class="sr-only"
            />
            <span class="text-2xl">👛</span>
            <span class="font-bold text-xs text-white">Ví MoMo</span>
          </label>

          <label
            :class="[
              'p-4 rounded-2xl border flex flex-col items-center justify-center gap-2 cursor-pointer transition-all',
              selectedMethod === 'CARD'
                ? 'border-sky-500 bg-sky-500/10 ring-2 ring-sky-500/30'
                : 'border-slate-800 bg-slate-900/60 hover:border-slate-700',
            ]"
          >
            <input
              v-model="selectedMethod"
              type="radio"
              value="CARD"
              class="sr-only"
            />
            <span class="text-2xl">💳</span>
            <span class="font-bold text-xs text-white">Thẻ Visa / Master</span>
          </label>
        </div>
      </div>

      <!-- Submit Payment Button -->
      <button
        class="w-full py-4 px-8 rounded-2xl font-bold text-base text-white bg-gradient-to-r from-emerald-600 via-teal-600 to-sky-600 hover:from-emerald-500 hover:to-sky-500 disabled:opacity-40 disabled:cursor-not-allowed transition-all shadow-xl shadow-emerald-600/25 flex items-center justify-center gap-2"
        :disabled="isPaying || bookingStore.countdownSeconds <= 0"
        @click="handlePayment"
      >
        <span v-if="isPaying">Đang xử lý thanh toán...</span>
        <span v-else
          >💳 Thanh Toán Ngay ({{
            formatPrice(bookingStore.heldBooking.totalAmount)
          }})</span
        >
      </button>
    </div>

    <!-- Empty/Invalid booking fallback -->
    <div v-else class="glass-panel p-12 rounded-3xl text-center space-y-4">
      <div class="text-3xl">⚠️</div>
      <h3 class="text-lg font-bold text-white">
        Không tìm thấy đơn giữ chỗ hợp lệ
      </h3>
      <p class="text-xs text-slate-400">
        Vui lòng quay lại danh sách chuyến đi và chọn ghế.
      </p>
      <NuxtLink
        to="/"
        class="inline-block px-6 py-2.5 rounded-xl bg-indigo-600 text-white text-xs font-bold mt-2"
      >
        ← Quay Lại Tìm Chuyến
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useBookingStore } from "~/stores/booking";

const router = useRouter();
const config = useRuntimeConfig();
const bookingStore = useBookingStore();

const selectedMethod = ref("VNPAY");
const isPaying = ref(false);

const handlePayment = async () => {
  if (!bookingStore.heldBooking || isPaying.value) return;
  isPaying.value = true;

  const bookingId = bookingStore.heldBooking.bookingId;

  try {
    const payload = {
      bookingId: bookingId,
      bookingCode: bookingStore.heldBooking.bookingCode,
      tripId: bookingStore.currentTrip?.id || bookingStore.heldBooking.tripId,
      tripCode: bookingStore.currentTrip?.tripCode,
      routeName: bookingStore.currentTrip?.routeName,
      departureTime: bookingStore.currentTrip?.departureTime,
      customerName: bookingStore.heldBooking.customerName,
      customerPhone: bookingStore.heldBooking.customerPhone,
      customerEmail: bookingStore.heldBooking.customerEmail,
      seatNumbers: bookingStore.heldBooking.seatNumbers,
      paymentMethod: selectedMethod.value,
      amount: bookingStore.heldBooking.totalAmount,
      idempotencyKey: "IDEMP-" + bookingId,
    };

    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/payments/checkout`,
      {
        method: "POST",
        body: payload,
      },
    );

    if (res && res.success) {
      bookingStore.clearHeldBooking();
      router.push(`/tickets/${bookingId}`);
    } else {
      alert("Thanh toán không thành công, vui lòng thử lại!");
    }
  } catch (error: any) {
    console.error("Lỗi thanh toán:", error);
    const errorMsg =
      error?.data?.message ||
      "Lỗi khi kết nối với cổng thanh toán, vui lòng thử lại!";
    alert(errorMsg);
  } finally {
    isPaying.value = false;
  }
};

// Theo dõi nếu hết thời gian giữ chỗ 5 phút
watch(
  () => bookingStore.countdownSeconds,
  (newVal) => {
    if (newVal <= 0 && bookingStore.heldBooking) {
      alert(
        "⏱️ Thời gian giữ chỗ 5 phút đã kết thúc! Ghế đã được giải phóng tự động.",
      );
      bookingStore.clearHeldBooking();
      router.push("/");
    }
  },
);

onMounted(() => {
  if (!bookingStore.heldBooking) {
    router.push("/");
  }
});
</script>
