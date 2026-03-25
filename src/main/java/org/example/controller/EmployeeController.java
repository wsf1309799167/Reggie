package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //1、确认用户是否存在
        if(emp == null){
            return R.error("用户不存在");
        }
        //2、确认密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //3、确认用户是否禁用
        if(emp.getStatus() == 0){
            return R.error("用户已禁用");
        }
        //4、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        // 同时存入ThreadLocal
        BaseContext.setCurrentId(emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        // 清除ThreadLocal中的用户ID
        BaseContext.removeCurrentId();
        return R.success("退出成功");

    }

    @PostMapping("/saveEmployee")
    public R<String> saveEmployee(HttpServletRequest request,@RequestBody Employee employee){
        //1、设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //2、设置其他默认值
        employeeService.saveOrUpdate(employee);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page( @RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                         @RequestParam(value = "name", required = false) String name)
    {

        Page pageInfo =new Page(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Employee::getName,name);
        queryWrapper.orderByAsc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping("/updateEmployeeStatus")
    public R<String> updateEmployeeStatus(@RequestBody Employee employee){
        if(employee.getId() == null){
            return R.error("员工id不能为空");
        }
        if(employee.getStatus() == null){
            return R.error("员工状态不能为空");
        }
        // 使用updateById方法，这样会触发MyMetaObjectHandler的自动填充
        employeeService.updateById(employee);
        return R.success("员工状态更新成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

    @PutMapping("/update")
    public R<String> update( @RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("员工信息更新成功");
    }
}