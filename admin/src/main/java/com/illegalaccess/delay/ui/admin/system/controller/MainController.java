package com.illegalaccess.delay.ui.admin.system.controller;

import com.illegalaccess.delay.toolkit.dto.BaseResponse;
import com.illegalaccess.delay.ui.admin.system.validator.UserValid;
import com.illegalaccess.delay.ui.client.DelayUIClient;
import com.illegalaccess.delay.ui.common.constant.AdminConst;
import com.illegalaccess.delay.ui.common.data.URL;
import com.illegalaccess.delay.ui.common.enums.ResultEnum;
import com.illegalaccess.delay.ui.common.enums.StatusEnum;
import com.illegalaccess.delay.ui.common.exception.ResultException;
import com.illegalaccess.delay.ui.common.utils.EntityBeanUtil;
import com.illegalaccess.delay.ui.common.utils.ResultVoUtil;
import com.illegalaccess.delay.ui.common.utils.SpringContextUtil;
import com.illegalaccess.delay.ui.common.vo.ResultVo;
import com.illegalaccess.delay.ui.component.shiro.ShiroUtil;
import com.illegalaccess.delay.ui.modules.system.domain.Menu;
import com.illegalaccess.delay.ui.modules.system.domain.Role;
import com.illegalaccess.delay.ui.modules.system.domain.Upload;
import com.illegalaccess.delay.ui.modules.system.domain.User;
import com.illegalaccess.delay.ui.modules.system.enums.MenuTypeEnum;
import com.illegalaccess.delay.ui.modules.system.service.MenuService;
import com.illegalaccess.delay.ui.modules.system.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 小懒虫
 * @date 2018/8/14
 */
@Controller
public class MainController{

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @DubboReference(interfaceClass = DelayUIClient.class)
    private DelayUIClient delayUIClient;

    /**
     * 后台主体内容
     */
    @GetMapping("/")
    @RequiresPermissions("index")
    public String main(Model model){
        // 获取当前登录的用户
        User user = ShiroUtil.getSubject();

        // 菜单键值对(ID->菜单)
        Map<Long, Menu> keyMenu = new HashMap<>(16);

        // 管理员实时更新菜单
        if(user.getId().equals(AdminConst.ADMIN_ID)){
            List<Menu> menus = menuService.getListBySortOk();
            menus.forEach(menu -> keyMenu.put(menu.getId(), menu));
        }else{
            // 其他用户需从相应的角色中获取菜单资源
            Set<Role> roles = ShiroUtil.getSubjectRoles();
            roles.forEach(role -> {
                role.getMenus().forEach(menu -> {
                    if(menu.getStatus().equals(StatusEnum.OK.getCode())){
                        keyMenu.put(menu.getId(), menu);
                    }
                });
            });
        }

        // 封装菜单树形数据
        Map<Long, Menu> treeMenu = new HashMap<>(16);
        keyMenu.forEach((id, menu) -> {
            if(!menu.getType().equals(MenuTypeEnum.BUTTON.getCode())){
                if(keyMenu.get(menu.getPid()) != null){
                    keyMenu.get(menu.getPid()).getChildren().put(Long.valueOf(menu.getSort()), menu);
                }else{
                    if(menu.getType().equals(MenuTypeEnum.DIRECTORY.getCode())){
                        treeMenu.put(Long.valueOf(menu.getSort()), menu);
                    }
                }
            }
        });

        model.addAttribute("user", user);
        model.addAttribute("treeMenu", treeMenu);
        return "/main";
    }

    /**
     * 主页
     */
    @GetMapping("/index")
    @RequiresPermissions("index")
    public String index(Model model){
        Long userNum = userService.validUserNumber();
        model.addAttribute("userNum", userNum);

        BaseResponse<Long> baseResponse = delayUIClient.queryTotalMessage();
        if (baseResponse.OK()) {
            model.addAttribute("totalMsg", baseResponse.getContent());
        } else {
            model.addAttribute("totalMsg", 0);
        }

        BaseResponse<Integer> appResp = delayUIClient.queryTotalAppKey();
        if (appResp.OK()) {
            model.addAttribute("appliedAppKeyCnt", appResp.getContent());
        } else {
            model.addAttribute("appliedAppKeyCnt", 0);
        }
        return "/system/main/index";
    }


    /**
     * 跳转到个人信息页面
     */
    @GetMapping("/userInfo")
    @RequiresPermissions("index")
    public String toUserInfo(Model model){
        User user = ShiroUtil.getSubject();
        model.addAttribute("user", user);
        return "/system/main/userInfo";
    }

    /**
     * 修改用户头像
     */
    @PostMapping("/userPicture")
    @RequiresPermissions("index")
    @ResponseBody
    public ResultVo userPicture(@RequestParam("picture") MultipartFile picture){
        UploadController uploadController = SpringContextUtil.getBean(UploadController.class);
        ResultVo imageResult = uploadController.uploadPicture(picture);
        if(imageResult.getCode().equals(ResultEnum.SUCCESS.getCode())){
            User subject = ShiroUtil.getSubject();
            subject.setPicture(((Upload) imageResult.getData()).getPath());
            userService.save(subject);
            return ResultVoUtil.SAVE_SUCCESS;
        }else {
            return imageResult;
        }
    }

    /**
     * 保存修改个人信息
     */
    @PostMapping("/userInfo")
    @RequiresPermissions("index")
    @ResponseBody
    public ResultVo userInfo(@Validated UserValid valid, User user){

        // 复制保留无需修改的数据
        User subUser = ShiroUtil.getSubject();
        String[] ignores = {"id", "username", "password", "salt", "picture", "dept", "roles"};
        EntityBeanUtil.copyPropertiesIgnores(user, subUser, ignores);

        // 保存数据
        userService.save(subUser);
        return ResultVoUtil.success("保存成功", new URL("/userInfo"));
    }

    /**
     * 跳转到修改密码页面
     */
    @GetMapping("/editPwd")
    @RequiresPermissions("index")
    public String toEditPwd(){
        return "/system/main/editPwd";
    }

    /**
     * 保存修改密码
     */
    @PostMapping("/editPwd")
    @RequiresPermissions("index")
    @ResponseBody
    public ResultVo editPwd(String original, String password, String confirm){
        // 判断原来密码是否有误
        User subUser = ShiroUtil.getSubject();
        String oldPwd = ShiroUtil.encrypt(original, subUser.getSalt());
        if (original.isEmpty() || "".equals(original.trim()) || !oldPwd.equals(subUser.getPassword())) {
            throw new ResultException(ResultEnum.USER_OLD_PWD_ERROR);
        }

        // 判断密码是否为空
        if (password.isEmpty() || "".equals(password.trim())) {
            throw new ResultException(ResultEnum.USER_PWD_NULL);
        }

        // 判断两次密码是否一致
        if (!password.equals(confirm)) {
            throw new ResultException(ResultEnum.USER_INEQUALITY);
        }

        // 修改密码，对密码进行加密
        String salt = ShiroUtil.getRandomSalt();
        String encrypt = ShiroUtil.encrypt(password, salt);
        subUser.setPassword(encrypt);
        subUser.setSalt(salt);

        // 保存数据
        userService.save(subUser);
        return ResultVoUtil.success("修改成功");
    }
}
