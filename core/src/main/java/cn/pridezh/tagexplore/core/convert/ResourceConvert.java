//package cn.pridezh.tagexplore.core.convert;
//
//import cn.pridezh.tagexplore.core.domain.vo.ResourceItemVO;
//import cn.pridezh.tagexplore.core.domain.po.Resource;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Named;
//import org.mapstruct.ReportingPolicy;
//
///**
// * @author PrideZH
// */
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public abstract class ResourceConvert {
//
//    @Mapping(source = "name", target = "name", qualifiedByName = "parse")
//    public abstract ResourceItemVO toVO(Resource resource);
//
//    @Named("parse")
//    public String parse(String name) {
//        byte[] bytes = name.substring(0, name.lastIndexOf('.') - 1).getBytes();
//        String password = "123456";
//        byte[] passwordBytes = password.getBytes();
//            for (int i = 0; i < bytes.length; i += passwordBytes.length) {
//            for (int j = 0; j < passwordBytes.length; j++) {
//                bytes[i + j] ^= passwordBytes[j];
//            }
//        }
//        return new String(bytes);
//    }
//
//}
