# Ortalama Vade Hesaplama - Android Projesi

Bu proje, `ORTALAMA VADE HESAPLAMA.xlsx` dosyasındaki hesaplama mantığını Android uygulamasına taşır.

## Mantık
- Her satır: Referans Tarihi, Ödeme Tarihi, Tutar.
- Vade (Gün) = Ödeme Tarihi - Referans Tarihi.
- Toplam Tutar = girilen tutarların toplamı.
- Ağırlıklı Ortalama Vade (Gün) = SUM(Tutar × Vade Gün) / SUM(Tutar).
- Ağırlıklı Ortalama Vade Tarihi = SUM(Tutar × Ödeme Tarihi) / SUM(Tutar), en yakın güne yuvarlanır.
- Dolu Ödeme Sayısı = ödeme tarihi girilmiş satır sayısı.

## Kullanım
Tarihleri `GG.AA.YYYY` biçiminde, tutarları klavye ile girin. Hesaplamalar otomatik güncellenir.

## Derleme
Android Studio'da bu klasörü açıp Gradle senkronizasyonu yaptıktan sonra `Build > Build APK(s)` seçilebilir. Proje Android Gradle Plugin 8.7.3, compileSdk 35, minSdk 23 kullanır.
