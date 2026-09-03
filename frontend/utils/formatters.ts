/**
 * Định dạng chuỗi ngày tháng ISO sang dạng hiển thị vi-VN (HH:mm dd/MM/yyyy)
 */
export const formatDateTime = (isoString?: string | null): string => {
  if (!isoString) return "";
  const date = new Date(isoString);
  if (isNaN(date.getTime())) return "";
  return date.toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

/**
 * Định dạng số tiền sang định dạng tiền tệ Việt Nam (VND)
 */
export const formatPrice = (amount?: number | null): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount || 0);
};
