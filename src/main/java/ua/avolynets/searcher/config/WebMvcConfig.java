package ua.avolynets.searcher.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;


    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("images/**").addResourceLocations("images/");
        registry.addResourceHandler("html/**").addResourceLocations("html/");


//        boolean devMode = this.env.acceptsProfiles("dev");
//        boolean useResourceCache = !devMode;
//        Integer cachePeriod = devMode ? 0 : null;
//
//        registry.addResourceHandler("/html/**")
//                .addResourceLocations("/html/", "classpath:/html/");
//                .setCachePeriod(cachePeriod)
//                .resourceChain(useResourceCache)
//                .addResolver(new GzipResourceResolver())
//                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
//                .addTransformer(new AppCacheManifestTransformer());
    }

//    @Bean
//    public ViewResolver setupViewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("WEB-INF/views/");
//        resolver.setSuffix(".jsp");
//        resolver.setViewClass(JstlView.class);
//        return resolver;
//    }
}