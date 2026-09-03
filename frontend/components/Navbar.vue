<template>
  <header class="sticky top-0 z-50 glass-panel border-b border-slate-800/80">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      <!-- Logo -->
      <NuxtLink to="/" class="flex items-center gap-3 group">
        <div class="w-10 h-10 rounded-xl overflow-hidden shadow-lg shadow-indigo-500/25 group-hover:scale-105 transition-transform flex items-center justify-center bg-slate-900 border border-slate-800">
          <img src="/logo.png" alt="Logo" class="w-full h-full object-cover" />
        </div>
        <div>
          <span class="font-display font-bold text-xl tracking-tight text-white group-hover:text-indigo-400 transition-colors">
            Ticket<span class="gradient-text">Stream</span>
          </span>
          <span class="block text-[10px] text-slate-400 uppercase tracking-widest font-semibold">Real-Time Booking</span>
        </div>
      </NuxtLink>

      <!-- Navigation & Active Hold Timer -->
      <div class="flex items-center gap-4">
        <!-- Active Hold Timer Pill -->
        <NuxtLink 
          v-if="bookingStore.heldBooking && bookingStore.countdownSeconds > 0"
          to="/checkout"
          class="flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-sm font-semibold animate-pulse"
        >
          <span class="w-2 h-2 rounded-full bg-amber-400 animate-ping"></span>
          <span>Giữ chỗ: {{ bookingStore.formattedTimeLeft }}</span>
          <span class="text-xs bg-amber-500/20 px-2 py-0.5 rounded-md">Thanh toán</span>
        </NuxtLink>

        <NuxtLink to="/" class="text-sm font-medium text-slate-300 hover:text-white transition-colors hidden sm:block">
          Tìm Chuyến
        </NuxtLink>
        <NuxtLink to="/" class="text-sm font-medium text-slate-400 hover:text-white transition-colors hidden sm:block">
          Lịch Trình
        </NuxtLink>
        
        <div class="h-4 w-px bg-slate-800 hidden sm:block"></div>

        <div class="flex items-center gap-2 text-xs bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-lg text-slate-300">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          <span class="font-mono">{{ bookingStore.userId }}</span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useBookingStore } from '~/stores/booking'
const bookingStore = useBookingStore()
</script>
