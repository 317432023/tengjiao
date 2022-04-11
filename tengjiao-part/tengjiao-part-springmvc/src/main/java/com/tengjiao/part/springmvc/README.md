
# springmvc参数验证

    方法一：在代码逻辑里取出对象里的这个值，手动进行判断，适用在页面输出的场景，返回结果的状态是 200
    方法二：使用@Valid注解，在接口请求的时候让Spring自己进行判断，适用接口之间调用，返回结果状态是 400
    
    方法二最好配合 GlobalExceptionHandler 进行使用

## 方法二示例
    @Data
    public class User {
        @NotNull(message = "用户id不能为空")
        private Long id;
    
        @NotNull(message = "用户账号不能为空")
        @Size(min = 6, max = 11, message = "账号长度必须是6-11个字符")
        private String account;
    
        @NotNull(message = "用户密码不能为空")
        @Size(min = 6, max = 11, message = "密码长度必须是6-16个字符")
        private String password;
    
        @NotNull(message = "用户邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;
    }
    
    @RestController
    @RequestMapping("user")
    public class UserController {
        @Autowired
        private UserService userService;
    
        @PostMapping("/addUser")
        public String addUser(@RequestBody @Valid User user, BindingResult bindingResult) {
            // 如果有参数校验失败，会将错误信息封装成对象组装在BindingResult里
            for (ObjectError error : bindingResult.getAllErrors()) {
                return error.getDefaultMessage();
            }
            return userService.addUser(user);
        }
    }

## @Valid使用说明

    @Valid注解可以实现数据的验证，在实体的属性上添加校验规则，而在API接收数据时添加@Valid关键字，这时实体将会开启一个校验的功能。Spring-boot可能目前并不支持对参数的验证
    
    @Valid 注解类型的使用:
    
    @Null
    限制只能为null
    
    @NotNull
    限制必须不为null
    
    @AssertFalse
    限制必须为false
    
    @AssertTrue
    限制必须为true
    
    @DecimalMax(value)
    限制必须为一个不大于指定值的数字
    
    @DecimalMin(value)
    限制必须为一个不小于指定值的数字
    
    @Digits(integer,fraction)
    限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
    
    @Future
    限制必须是一个将来的日期
    
    @Max(value)
    限制必须为一个不大于指定值的数字
    
    @Min(value)
    限制必须为一个不小于指定值的数字
    
    @Past
    限制必须是一个过去的日期
    
    @Pattern(value)
    限制必须符合指定的正则表达式
    
    @Size(max,min)
    限制字符长度必须在min到max之间
    
    @Past
    验证注解的元素值（日期类型）比当前时间早
    
    @NotEmpty
    验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
    
    @NotBlank
    验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
    
    @Email
    验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式

# 全局处理响应数据

    每一个接口返回数据时都要用响应体来包装一下好像有点麻烦，使用以下方法省去这个包装过程

    @ControllerAdvice(basePackages = {"com.rudecrab.demo.controller"}) // 注意哦，这里要加上需要扫描的包
    public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
            // 如果接口返回的类型本身就是Result那就没有必要进行额外的操作，返回false
            return !returnType.getGenericParameterType().equals(Result.class);
        }
    
        @Override
        public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
            // String类型不能直接包装，所以要进行些特别的处理
            if (returnType.getGenericParameterType().equals(String.class)) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // 将数据包装在ResultVO里后，再转换为json字符串响应给前端
                    return objectMapper.writeValueAsString(new Result<>(data));
                } catch (JsonProcessingException e) {
                    throw new SystemException("返回String类型错误");
                }
            }
            // 将原本的数据包装在ResultVO里
            return new Result<>(data);
        }
    }