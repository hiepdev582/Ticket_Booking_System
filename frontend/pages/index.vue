<template>
  <div class="space-y-10">
    <!-- Hero Banner & Search Form -->
    <section
      class="relative rounded-3xl overflow-hidden glass-panel p-8 sm:p-12 border border-slate-800 shadow-2xl"
    >
      <div
        class="absolute -top-24 -right-24 w-96 h-96 bg-indigo-500/15 rounded-full blur-3xl pointer-events-none"
      ></div>
      <div
        class="absolute -bottom-24 -left-24 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"
      ></div>

      <div class="relative max-w-3xl space-y-4">
        <div
          class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/30 text-indigo-300 text-xs font-semibold"
        >
          <span>⚡ Hệ thống Đặt Vé Cao Tốc & Tàu Hỏa Thời Gian Thực</span>
        </div>
        <h1
          class="text-3xl sm:text-5xl font-extrabold tracking-tight text-white font-display"
        >
          Đặt vé nhanh chóng, <br />
          <span class="gradient-text">Giữ chỗ trong 5 phút</span>
        </h1>
      </div>

      <!-- Search Input Bar -->
      <div
        class="mt-8 bg-slate-900/90 p-4 sm:p-6 rounded-2xl border border-slate-800 shadow-xl grid grid-cols-1 sm:grid-cols-3 gap-4"
      >
        <div>
          <label
            for="departure"
            class="block text-xs font-semibold text-slate-400 mb-1.5"
            >Điểm khởi hành</label
          >
          <input
            v-model="searchDeparture"
            id="departure"
            type="text"
            placeholder="Ví dụ: Sài Gòn, Hà Nội..."
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-sm text-white placeholder:text-slate-600 focus:outline-none focus:border-indigo-500 transition-colors"
          />
        </div>
        <div>
          <label
            for="arrival"
            class="block text-xs font-semibold text-slate-400 mb-1.5"
            >Điểm đến</label
          >
          <input
            v-model="searchArrival"
            id="arrival"
            type="text"
            placeholder="Ví dụ: Đà Lạt, Sa Pa, Huế..."
            class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-sm text-white placeholder:text-slate-600 focus:outline-none focus:border-indigo-500 transition-colors"
          />
        </div>
        <div class="flex items-end">
          <button
            class="w-full bg-gradient-to-r from-indigo-600 to-sky-600 hover:from-indigo-500 hover:to-sky-500 text-white font-semibold py-2.5 px-6 rounded-xl transition-all shadow-lg shadow-indigo-600/25 flex items-center justify-center gap-2"
            :disabled="isLoading"
            @click="fetchTrips"
          >
            <span v-if="isLoading">Đang tìm...</span>
            <span v-else>🔍 Tìm Chuyến Đi</span>
          </button>
        </div>
      </div>
    </section>

    <!-- Trip List Section -->
    <section class="space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-white font-display">
            Danh Sách Tuyến Đường
          </h2>
        </div>
        <div
          class="text-xs text-slate-400 bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-lg"
        >
          Tổng số:
          <strong class="text-indigo-400">{{ trips.length }}</strong> chuyến
        </div>
      </div>

      <!-- Loading State -->
      <div
        v-if="isLoading"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        <div
          v-for="n in 3"
          :key="n"
          class="glass-panel p-6 rounded-2xl border border-slate-800 animate-pulse space-y-4"
        >
          <div class="h-6 bg-slate-800 rounded-md w-3/4"></div>
          <div class="h-4 bg-slate-800 rounded-md w-1/2"></div>
          <div class="h-10 bg-slate-800 rounded-md w-full"></div>
        </div>
      </div>

      <!-- Trip Cards Grid -->
      <div
        v-else-if="trips.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        <div
          v-for="trip in trips"
          :key="trip.id"
          class="glass-panel glass-panel-hover p-6 rounded-2xl border border-slate-800/80 flex flex-col justify-between space-y-6 group"
        >
          <div>
            <!-- Header: Operator & Vehicle Type -->
            <div class="flex items-center justify-between mb-3">
              <span
                class="text-xs font-bold px-2.5 py-1 rounded-md bg-indigo-500/10 text-indigo-400 border border-indigo-500/20"
              >
                {{ trip.operatorName }}
              </span>
              <span
                class="text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300"
              >
                {{ trip.vehicleType === "BUS" ? "🚌 Xe Khách" : "🚆 Tàu Hỏa" }}
              </span>
            </div>

            <!-- Route Title -->
            <h3
              class="text-lg font-bold text-white group-hover:text-indigo-400 transition-colors"
            >
              {{ trip.routeName }}
            </h3>
            <p class="text-xs text-slate-400 mt-1 font-mono">
              Mã chuyến: {{ trip.tripCode }}
            </p>

            <!-- Timeline -->
            <div
              class="mt-4 pt-4 border-t border-slate-800/80 space-y-2 text-xs"
            >
              <div class="flex items-center justify-between text-slate-300">
                <span class="text-slate-500">Khởi hành:</span>
                <span class="font-semibold text-white">{{
                  formatDateTime(trip.departureTime)
                }}</span>
              </div>
              <div class="flex items-center justify-between text-slate-300">
                <span class="text-slate-500">Điểm đón:</span>
                <span
                  class="text-right truncate max-w-[200px]"
                  :title="trip.departureLocation"
                  >{{ trip.departureLocation }}</span
                >
              </div>
              <div class="flex items-center justify-between text-slate-300">
                <span class="text-slate-500">Điểm đến:</span>
                <span
                  class="text-right truncate max-w-[200px]"
                  :title="trip.arrivalLocation"
                  >{{ trip.arrivalLocation }}</span
                >
              </div>
            </div>
          </div>

          <!-- Bottom: Seats & Price -->
          <div
            class="pt-4 border-t border-slate-800/80 flex items-center justify-between"
          >
            <div>
              <div class="text-[11px] text-slate-400">Còn trống</div>
              <div
                class="text-sm font-bold text-emerald-400 flex items-center gap-1.5"
              >
                <span
                  class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"
                ></span>
                {{ trip.availableSeats }} / {{ trip.totalSeats }} ghế
              </div>
            </div>

            <div class="text-right">
              <div class="text-[11px] text-slate-400">Giá vé từ</div>
              <div class="text-lg font-black text-white gradient-text">
                {{ formatPrice(trip.price) }}
              </div>
            </div>
          </div>

          <!-- Action Button -->
          <NuxtLink
            :to="`/trips/${trip.id}`"
            class="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-2.5 rounded-xl text-center text-sm transition-all shadow-md shadow-indigo-600/20 block"
          >
            Chọn Ghế & Giữ Chỗ ➔
          </NuxtLink>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="glass-panel p-12 rounded-2xl text-center space-y-4">
        <div class="text-4xl">🚌</div>
        <h3 class="text-lg font-bold text-white">
          Không tìm thấy chuyến đi phù hợp
        </h3>
        <p class="text-sm text-slate-400">
          Vui lòng thử tìm kiếm với điểm khởi hành hoặc điểm đến khác.
        </p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { type Trip } from "~/stores/booking";

const config = useRuntimeConfig();
const trips = ref<Trip[]>([]);
const isLoading = ref(false);

const searchDeparture = ref("");
const searchArrival = ref("");

const fetchTrips = async () => {
  isLoading.value = true;
  try {
    const params = new URLSearchParams();
    if (searchDeparture.value)
      params.append("departure", searchDeparture.value);
    if (searchArrival.value) params.append("arrival", searchArrival.value);

    const res: any = await $fetch(
      `${config.public.apiBase}/api/v1/trips/search?${params.toString()}`,
    );
    if (res && res.success && res.data) {
      trips.value = res.data;
    } else {
      trips.value = [];
    }
  } catch (error) {
    console.error("Lỗi khi tải danh sách chuyến đi từ API Gateway:", error);
    trips.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchTrips();
});
</script>
