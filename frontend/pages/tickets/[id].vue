<template>
  <div class="max-w-3xl mx-auto space-y-8">
    <!-- Success Celebration Banner -->
    <div
      class="glass-panel p-8 rounded-3xl border border-emerald-500/30 bg-emerald-500/5 text-center space-y-3 relative overflow-hidden shadow-2xl"
    >
      <div
        class="w-16 h-16 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-400 text-3xl font-bold flex items-center justify-center mx-auto shadow-lg shadow-emerald-500/20"
      >
        ✓
      </div>
      <h1 class="text-2xl sm:text-3xl font-extrabold text-white font-display">
        Đặt Vé & Thanh Toán Thành Công!
      </h1>
      <p class="text-xs sm:text-sm text-slate-300 max-w-lg mx-auto">
        Vé điện tử và mã QR định danh của bạn đã được khởi tạo và lưu trữ an
        toàn trên MinIO S3 Object Storage.
      </p>
    </div>

    <!-- Boarding Pass Ticket Cards -->
    <div v-if="tickets.length > 0" class="space-y-6">
      <div
        v-for="ticket in tickets"
        :key="ticket.id"
        class="glass-panel rounded-3xl border border-slate-800 overflow-hidden shadow-2xl relative flex flex-col md:flex-row"
      >
        <!-- Left Part: Ticket Info -->
        <div class="p-6 sm:p-8 flex-1 space-y-6">
          <div class="flex items-center justify-between">
            <span
              class="text-xs font-bold px-3 py-1 rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30"
            >
              VÉ ĐIỆN TỬ XE KHÁCH / TÀU HỎA
            </span>
            <span class="text-xs font-mono text-slate-400"
              >Mã: {{ ticket.ticketNumber }}</span
            >
          </div>

          <div class="grid grid-cols-2 gap-4 text-xs">
            <div>
              <div class="text-slate-500">Hành khách</div>
              <div class="text-white font-bold text-sm mt-0.5">
                {{ ticket.passengerName || "Khách hàng" }}
              </div>
            </div>
            <div>
              <div class="text-slate-500">Số điện thoại</div>
              <div class="text-white font-bold text-sm mt-0.5">
                {{ ticket.passengerPhone || "" }}
              </div>
            </div>
            <div>
              <div class="text-slate-500">Tuyến đường</div>
              <div class="text-indigo-300 font-bold text-sm mt-0.5">
                {{ ticket.routeName }}
              </div>
            </div>
            <div>
              <div class="text-slate-500">Vị trí ghế</div>
              <div class="text-emerald-400 font-extrabold text-lg mt-0.5">
                Ghế {{ ticket.seatNumber }}
              </div>
            </div>
            <div>
              <div class="text-slate-500">Thời gian xuất bến</div>
              <div class="text-white font-semibold mt-0.5">
                {{ formatDateTime(ticket.departureTime) }}
              </div>
            </div>
            <div>
              <div class="text-slate-500">Trạng thái vé</div>
              <div class="text-emerald-400 font-semibold mt-0.5">
                ✓ ĐÃ XUẤT VÉ (PAID)
              </div>
            </div>
          </div>

          <!-- Download Action -->
          <div class="pt-4 border-t border-slate-800/80 flex flex-wrap gap-3">
            <a
              :href="
                ticket.pdfDownloadUrl ||
                `${config.public.apiBase}/api/v1/tickets/${ticket.id}/download`
              "
              target="_blank"
              class="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs transition-all shadow-lg shadow-indigo-600/25 flex items-center gap-2"
            >
              📄 Tải File PDF Vé
            </a>
          </div>
        </div>

        <!-- Right Part: QR Code & Verification (Stub) -->
        <div
          class="p-6 bg-slate-900/90 border-t md:border-t-0 md:border-l border-slate-800/80 flex flex-col items-center justify-center text-center space-y-3 min-w-[200px]"
        >
          <!-- QR Code Preview -->
          <div
            class="w-28 h-28 bg-white p-2 rounded-2xl shadow-lg flex items-center justify-center"
          >
            <img
              alt="QR Code Vé"
              class="w-full h-full object-contain"
              :src="`https://api.qrserver.com/v1/create-qr-code/?size=120x120&data=${encodeURIComponent('TICKET:' + ticket.ticketNumber + '|SEAT:' + ticket.seatNumber)}`"
            />
          </div>
          <div class="text-[11px] text-slate-400 font-mono">
            Quét mã khi lên xe
          </div>
          <div class="text-xs font-bold text-slate-200">
            {{ formatPrice(ticket.price) }}
          </div>
        </div>
      </div>
    </div>

    <!-- Empty/Loading State -->
    <div v-else class="glass-panel p-12 rounded-3xl text-center space-y-4">
      <div class="text-3xl animate-spin">⏳</div>
      <h3 class="text-lg font-bold text-white">
        Đang xử lý xuất vé điện tử...
      </h3>
      <button
        class="px-5 py-2 rounded-xl bg-indigo-600 text-white text-xs font-bold"
        @click="fetchTickets"
      >
        🔄 Tải lại vé
      </button>
    </div>

    <!-- Return Home Button -->
    <div class="text-center pt-4">
      <NuxtLink
        to="/"
        class="inline-flex items-center gap-2 px-6 py-3 rounded-2xl bg-slate-900 border border-slate-800 text-sm font-semibold text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
      >
        ← Quay Lại Trang Chủ
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute();
const config = useRuntimeConfig();
const id = route.params.id as string;

const tickets = ref<any[]>([]);

const fetchTickets = async () => {
  try {
    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/tickets/booking/${id}`,
    );
    if (res && res.success && res.data && res.data.length > 0) {
      tickets.value = res.data;
    } else {
      tickets.value = [];
    }
  } catch (error) {
    console.error("Lỗi khi lấy thông tin vé từ API:", error);
    tickets.value = [];
  }
};

onMounted(() => {
  fetchTickets();
});
</script>
