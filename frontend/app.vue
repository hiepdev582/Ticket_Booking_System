<template>
  <div class="min-h-screen bg-slate-950 flex flex-col justify-between">
    <div>
      <Navbar />
      <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6 pb-16">
        <NuxtPage />
      </main>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Navbar from "~/components/Navbar.vue";
import Footer from "~/components/Footer.vue";
</script>
