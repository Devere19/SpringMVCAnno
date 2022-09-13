# 基于注解实现MVC流程

结构

- ioc

    - AnnotationBeanFactory                  把dao层、service层、controller层对象通过反射创建，并且把属性注入，还提供一个getBean方法，这里dispatcherServlet中调用方法的时候用得到。因为反射调用对象需要的参数是对象和参数，通过id去获取这个bean。

      因为这是我自己使用的，所以我就实现的时候把dao、service、controller的全类名直接定义成了常量，因为我能保证我用的时候就在那个包下写，等事件空闲之后我打算用配置文件实现，当然这个很简单。

- mvc       实现mvc功能的主要包

    - annotation  该包下时自定义得注解类

        - @Controller---------------------------------自定义控制器注解
        - RequestMapping--------------------------自定义接收url注解
        - Resource-------------------------------------自定义注入注解
        - Service----------------------------------------自定义服务层注解

    - **Configuration**---------这个类就是把所有的Controller包里面所有的带有@Controller和@RequestMapping的方法存入到一个全局map中，到时候通过url去里面招哪个能处理该请求。

      实现的时候就定义好了controller所在的包的名称，直接遍历这个文件夹中的所有文件，如果是以class结尾的，并且里面带有@RequesMapping和@Controller的话，把RequestMapping里面value属性的值当作key，ControllerMapping作为value存储在Map中

    - **ContextConfigListener**----------Context容器监听器，主要作用就是1、在容器创建的时候去把dao层、service层、controller层的类创建好，放到容器里面；Class.forName(AnnoBeanAactory.class)2、获取到存取的controller存取的哪个类能处理哪个方法的Map，并且把这个Map放到全局作用域里面；调用Configuration的config方法，拿到这个Map并且放到全局作用域里面。request.getServletContext().setAttribute（）方法。

    - ControllerMapping类------------------包装类，存储controller的class文件和方法名，到时候在接受请求的时候就可以判断哪个controlller的哪个方法可以处理该请求。这个到时候存取的时候时根据@RequestMapping注解和@Controller来判断是否存进去的。

    - **DispatcherServlet**---------就是一个servlet类，继承HttpServlet。这个类就是处理url请求的，当url请求过来之后，这里面捕获到，然后查看Configutation类提供的map中有没有能处理这个请求的方法。所以在重写init（）方法中要获取到这个map。

      接下来就是对于url的处理，获取到url，然后就去这个map找是否有这个key，如果有的话就把这个key对应的value拿出来，是存取的ControllerMapping，目的就是执行这个里面存储的那个类的方法，**反射调用方法需要参数、对象**，因此接下来就是获取参数、对象和方法通过。参数是一个数组。

      获取对象就是AnnoBeanFactory的getBean方法拿到这个对象，

      获取方法参数就是获取参数类型（返回的是一个数组）、再获取参数名称，把参数名称放在一个list集合里面，

      接下来就是把网址栏中的参数赋值给方法的形参：定义一个数组用来存储赋值的数据，使用for循环进行赋值，如果是HttpServletRequest类型，就把这个request赋值到这个数组，如果是基本数据类型，类型是int，也存在数组中，如果是String，就存String。难的就是对象的处理，如果是对象的话，就把这个参数类型的实例通过反射创建出来，通过request.getParameterMap拿到键值对，再通过BeanUtils工具类进行反射赋值。赋值之后再放到数组中。

      方法、对象、参数都拿到了，就可以通过反射调用方法了。调用invoke(对象，参数数组)；返回一个Object对象结果，

      对Object对象结果进行判断，如果是String，则需要根据他里面是不是redirect判断是重定向还是转发，

      如果不是String，则就Gson把结果转换成json字符串输出。

        

