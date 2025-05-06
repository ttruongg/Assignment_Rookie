import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/scrollbar';
import 'swiper/css/effect-fade';
import 'swiper/css/autoplay';
import 'swiper/css';

import { Autoplay, Pagination, EffectFade, Navigation } from 'swiper/modules';
import { bannerLists } from '../../utils';

const HeroBanner = () => {
    return (
        <div className='py-2 rounded-md'>
            <Swiper
                grabCursor={true}
                autoplay={{
                    delay: 4000,
                    disableOnInteraction: false,
                }}
                navigation
                modules={[Pagination, EffectFade, Navigation, Autoplay]}
                pagination={{ clickable: true }}
                scrollbar={{ draggable: true }}
                slidesPerView={1}
                effect="fade"
            >
                {bannerLists.map((item) => (
                    <SwiperSlide key={item.id}>
                        <div className="w-full h-96 sm:h-[500px] rounded-md overflow-hidden">
                            <img
                                src={item.image}
                                alt={`banner-${item.id}`}
                                className="w-full h-full object-cover"
                            />
                        </div>
                    </SwiperSlide>
                ))}
            </Swiper>
        </div>
    );
};

export default HeroBanner;
