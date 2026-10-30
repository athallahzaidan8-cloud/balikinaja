package com.balikinaja.config;

import com.balikinaja.service.storage.PenyimpananFotoLokal;
import java.util.Locale;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AksesInterceptor aksesInterceptor;
    private final ObjectProvider<PenyimpananFotoLokal> penyimpananLokal;

    public WebConfig(AksesInterceptor aksesInterceptor, ObjectProvider<PenyimpananFotoLokal> penyimpananLokal) {
        this.aksesInterceptor = aksesInterceptor;
        this.penyimpananLokal = penyimpananLokal;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aksesInterceptor)
                .addPathPatterns("/dashboard", "/lapor", "/laporan/saya", "/laporan/hilang/**", "/laporan/temuan/**",
                        "/klaim", "/klaim/**", "/admin", "/admin/**");
    }

    /** Tanggal selalu ditampilkan dalam bahasa Indonesia (mis. "30 Okt 2026"). */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.forLanguageTag("id-ID"));
    }

    /**
     * Mode lokal: foto disajikan dari folder upload lewat URL /uploads/nama-file.
     * Mode database: URL yang sama dilayani FotoController.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        penyimpananLokal.ifAvailable(lokal -> registry.addResourceHandler("/uploads/**")
                .addResourceLocations(lokal.getFolderUpload().toUri().toString()));
    }
}
