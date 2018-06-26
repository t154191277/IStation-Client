package wmlove.istation.entity;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wmlove on 2018/4/8.
 */

public class Country {

    public List<CountryArea> countries;

    public class CountryArea {
        public String areaShort = "";
        public List<CountryBean> beanList = new ArrayList<>();
    }

    public class CountryBean {
        public String area = "";
        public String area_code = "";
    }

    public static class CountryBeanView {
        public String areaShort = "";
        public String area = "";
        public String area_code = "";
    }


    public static List<CountryBeanView> covertCountryBeanView(Country country){
        List<CountryBeanView> listNew = new ArrayList<>();
        if (country.countries == null){
            return listNew;
        }

        for (CountryArea cArea : country.countries){

            CountryBeanView viewBean = new CountryBeanView();
            for (CountryBean bean : cArea.beanList){

                viewBean.areaShort = TextUtils.isEmpty(cArea.areaShort) ? "" : cArea.areaShort;
                viewBean.area = TextUtils.isEmpty(bean.area) ? "" : bean.area;
                viewBean.area_code = TextUtils.isEmpty(bean.area_code) ? "" : bean.area_code;
                listNew.add(viewBean);
            }
        }

        return listNew;
    }

    public static List<Country.CountryBeanView> covert(Bean bean){
        List<CountryBeanView> listNew = new ArrayList<>();
        if (bean == null){
            return listNew;
        }

        Field[] fields = bean.getClass().getDeclaredFields();
        try {
            for (Field field : fields){
                field.getGenericType();
                List<Bean.XBean> list = (List<Bean.XBean>)field.get(bean);

                for (Bean.XBean xBean : list){
                    CountryBeanView viewBean = new CountryBeanView();
                    viewBean.areaShort = field.getName();
                    viewBean.area = TextUtils.isEmpty(xBean.area) ? "" : xBean.area;
                    viewBean.area_code = TextUtils.isEmpty(xBean.area_code) ? "" : xBean.area_code;
                    listNew.add(viewBean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return listNew;
    }
}
