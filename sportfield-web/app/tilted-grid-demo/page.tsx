import { TiltedGridHero } from "@/components/ui/tilted-grid-hero";

const IMAGES = [
  {
    src: "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800&h=450&fit=crop",
    alt: "Sport field landscape",
  },
  {
    src: "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800&h=450&fit=crop",
    alt: "Basketball court",
  },
  {
    src: "https://images.unsplash.com/photo-1518611505868-48510c2e022f?w=800&h=450&fit=crop",
    alt: "Tennis court",
  },
  {
    src: "https://images.unsplash.com/photo-1525861551033-fbe1949ea1a8?w=800&h=450&fit=crop",
    alt: "Badminton court",
  },
  {
    src: "https://images.unsplash.com/photo-1447710326928-7d434b3a9d79?w=800&h=450&fit=crop",
    alt: "Soccer field",
  },
  {
    src: "https://images.unsplash.com/photo-1518611505868-48510c2e022f?w=800&h=450&fit=crop",
    alt: "Volleyball court",
  },
];

export default function TiltedGridDemoPage() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800">
      <TiltedGridHero
        images={IMAGES}
        className="h-[560px] w-full rounded-lg border border-slate-700 bg-slate-800"
      >
        <div className="relative z-10 flex h-full flex-col items-center justify-between py-12 text-center">
          <div className="px-6">
            <h1 className="text-balance text-4xl font-medium tracking-tight text-white sm:text-5xl">
              Pesan Lapangan Olahraga
              <br />
              Dengan Mudah
            </h1>
          </div>
          <p className="max-w-md text-balance px-6 text-sm text-slate-300">
            Pilih dari berbagai lapangan olahraga terbaik di kota Anda dan reservasi dengan sekali klik.
          </p>
        </div>
      </TiltedGridHero>
    </main>
  );
}
