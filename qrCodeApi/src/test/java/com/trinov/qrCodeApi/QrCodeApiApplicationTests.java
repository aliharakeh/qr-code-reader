package com.trinov.qrCodeApi;

import com.trinov.qrCodeApi.enums.Colors;
import com.trinov.qrCodeApi.services.CompressionService;
import com.trinov.qrCodeApi.services.QRCodeService;
import com.trinov.qrCodeApi.services.UtilsService;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import org.junit.jupiter.api.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.QRCodeDetector;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SpringBootTest
class QrCodeApiApplicationTests {

    @Autowired
    QRCodeService qrCodeService;

    @Autowired
    CompressionService compressionService;

    @Autowired
    UtilsService utilsService;

    String data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam eleifend ligula et mauris feugiat blandit. Praesent a tortor sed nibh interdum dictum. Vestibulum posuere lectus ac purus eleifend auctor. Etiam viverra venenatis tortor in luctus. Nunc tristique nunc sit amet risus blandit, vitae venenatis massa pharetra. Fusce sollicitudin id urna id mattis. Vivamus gravida, enim nec blandit hendrerit, diam est congue nisi, facilisis iaculis odio nunc in massa.\n" +
            "\n" +
            "Phasellus porttitor, neque vel aliquet dignissim, odio arcu varius massa, in lobortis ex lacus eget diam. Praesent volutpat lacus quis imperdiet viverra. Integer ut libero varius, scelerisque ligula id, porttitor nibh. Sed quis dui quis neque fringilla egestas. Ut orci tellus, ornare a dui et, fermentum laoreet nibh. Fusce eget eros erat. Pellentesque sed bibendum magna. Morbi sollicitudin, libero a ultrices ultrices, enim nulla feugiat arcu, quis pulvinar lacus ligula quis eros. Ut aliquet ante a quam blandit, et feugiat ante semper. Proin euismod est ligula, non hendrerit metus laoreet non. Nulla malesuada egestas pretium. Maecenas ultricies lorem vitae consectetur mattis.\n" +
            "\n" +
            "Integer iaculis velit vel ligula varius, id hendrerit lacus interdum. In vel ante in nulla finibus sollicitudin. Ut lacinia ultrices nulla at placerat. Proin euismod congue arcu, nec dapibus enim molestie vehicula. Ut mollis varius pharetra. Mauris aliquam sollicitudin aliquam. Phasellus hendrerit imperdiet quam, eu auctor orci iaculis sit amet. Curabitur mattis dapibus vulputate. Cras porttitor vel sapien at dictum. Donec ullamcorper risus et posuere venenatis. Praesent eget lectus erat.\n" +
            "\n" +
            "Etiam blandit nulla et ex ultrices, eu sagittis mauris blandit. Vestibulum vel dignissim ex, id cursus metus. Fusce massa nulla, efficitur id sagittis in, ornare sit amet lorem. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Quisque sem metus, condimentum vitae mauris et, feugiat fermentum risus. Duis ullamcorper justo orci, quis placerat est blandit in. Aenean vitae risus vitae ante congue ultricies malesuada luctus ex. Donec nec vehicula mauris, id convallis leo. Donec vitae diam fringilla justo vestibulum sagittis. Nam a volutpat nisi, ut accumsan quam. Aliquam sollicitudin iaculis pretium. In pellentesque metus eget leo bibendum mattis. Proin nec eleifend massa. Quisque facilisis enim sit amet sagittis interdum.\n" +
            "\n" +
            "Maecenas finibus volutpat ipsum. Proin suscipit hendrerit dapibus. Cras nec nulla id magna sodales mollis id id erat. Suspendisse a urna et felis consequat malesuada. Vivamus eu blandit elit, a lacinia lacus. Sed eleifend purus at suscipit vestibulum. Quisque nec erat nibh. Praesent sit amet leo at tellus euismod ornare. Suspendisse vel mi non justo aliquet viverra. Ut sit amet justo viverra, lobortis ante posuere, fringilla nibh. Quisque euismod convallis viverra. Cras mattis mollis risus eget ultricies. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam erat volutpat.\n" +
            "\n" +
            "Pellentesque tristique rutrum nisl, sit amet sagittis augue sagittis non. Suspendisse lobortis ornare sapien. Ut justo ante, venenatis nec sodales in, tincidunt ac elit. Donec ornare mauris eget pulvinar semper. Sed venenatis ante nisi. Quisque suscipit feugiat dignissim. Nulla ac justo suscipit lectus viverra faucibus et eu lectus. Integer dignissim sodales euismod. Donec blandit ornare diam ut volutpat. Sed ullamcorper nulla lectus, id porttitor ante aliquam at. Morbi egestas nunc id placerat condimentum. Nullam at mauris vel quam finibus ultrices nec efficitur lectus. Cras aliquet massa non ultrices suscipit. Proin vel erat pulvinar, euismod velit id, mattis diam.\n" +
            "\n" +
            "Pellentesque auctor ligula convallis, vestibulum libero id, lacinia ante. Donec accumsan id massa nec aliquam. Aliquam leo nisl, gravida blandit maximus sed, lacinia sit amet ex. Nunc sapien nisl, auctor nec gravida ut, faucibus vel eros. Praesent fermentum mollis dictum. Donec pharetra id nulla sed maximus. Quisque commodo dolor eget justo convallis, et accumsan erat volutpat. Etiam non augue elit. Donec volutpat, risus et blandit facilisis, purus quam fermentum diam, in sagittis turpis tellus id nisl. In hac habitasse platea dictumst. Maecenas non imperdiet turpis.\n" +
            "\n" +
            "Curabitur egestas tempor lorem, a tempor magna ultrices in. Phasellus ut sagittis eros. Aliquam erat volutpat. Sed luctus ut diam vitae posuere. Mauris luctus fringilla lectus eu vulputate. Pellentesque sit amet ullamcorper elit. Morbi interdum varius arcu, ut mattis arcu euismod in. Mauris pharetra felis pretium ultrices dictum.\n" +
            "\n" +
            "Cras bibendum finibus ipsum placerat sagittis. Suspendisse sagittis dui ante, eu sodales eros posuere eu. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus et dui turpis. Aliquam eget purus eu urna lobortis rhoncus ut at lacus. Donec ligula ex, aliquet ultricies nibh in, placerat semper orci. Maecenas pulvinar dignissim tellus, at commodo libero vehicula quis. Praesent diam tellus, maximus vitae bibendum nec, dignissim ut mi. Suspendisse at aliquet augue. Aliquam consequat augue sit amet vestibulum consectetur.\n" +
            "\n" +
            "Integer lacinia accumsan odio. Vestibulum maximus fringilla nulla, et sodales metus aliquet eu. Vivamus quis rhoncus mi. Curabitur non interdum felis. Donec in turpis odio. Nam tincidunt dui justo, et ultrices justo commodo nec. Nullam ipsum eros, volutpat euismod nulla at, aliquet condimentum augue. Curabitur aliquam turpis vel leo viverra accumsan. Donec consectetur sagittis nunc, in laoreet ante rhoncus a. Praesent dignissim felis eu felis iaculis efficitur. Aliquam in dignissim sem. Duis bibendum auctor turpis quis convallis.\n" +
            "\n" +
            "Etiam hendrerit turpis malesuada, eleifend nisi et, semper mi. Duis non tellus venenatis, egestas nunc lacinia, mollis metus. Praesent a ipsum sed magna ornare vulputate. Etiam orci purus, dictum sed lorem quis, rutrum pharetra enim. Maecenas pellentesque nibh vel augue eleifend venenatis. Cras eu semper odio. Morbi vestibulum urna ornare semper mattis. In dictum eleifend tellus, quis luctus tellus vehicula id. Morbi metus erat, tempor eget velit vitae, egestas tempus eros. Pellentesque turpis magna, aliquam id sapien vel, facilisis feugiat urna.\n" +
            "\n" +
            "Duis pretium turpis mauris, non cursus nisl placerat euismod. Curabitur vel sapien et erat porttitor maximus eget in nulla. Sed pharetra sed diam non molestie. Suspendisse non lacus odio. Suspendisse nec commodo velit. Vestibulum lectus metus, pharetra vel tempus ut, congue id ex. Proin sagittis sollicitudin purus, vel porttitor enim accumsan nec. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed id turpis vitae nisl laoreet interdum. Aenean a feugiat erat. Sed bibendum vulputate augue, at condimentum diam rhoncus nec. Cras ac sem ex. Etiam congue erat nec risus interdum tincidunt. Aenean justo leo, viverra porta fermentum ac, euismod et metus.\n" +
            "\n" +
            "Vestibulum condimentum mi nisl, non accumsan velit sollicitudin non. Nulla ut convallis elit, vitae accumsan sem. Donec cursus, elit lobortis feugiat efficitur, nunc velit vehicula lectus, sit amet pellentesque nisi tortor venenatis lacus. Etiam maximus fringilla leo ac ullamcorper. Suspendisse vitae dolor lorem. Nulla justo ligula, placerat in ante at, sodales dignissim ipsum. Quisque faucibus, neque ac efficitur sagittis, mi tellus pharetra arcu, ut dapibus leo sapien vel ex. Praesent hendrerit purus ut blandit tincidunt. Nulla a libero neque. Donec mollis est lacinia ipsum porttitor laoreet. Suspendisse ut tellus tristique, porttitor dolor elementum, porta nulla.\n" +
            "\n" +
            "In augue ex, sollicitudin sed nunc at, auctor molestie metus. Vivamus massa nisi, faucibus id ullamcorper a, varius vel risus. Integer urna erat, gravida sollicitudin vestibulum sit amet, consequat vitae diam. Etiam cursus quis erat sed convallis. Sed eu turpis scelerisque, accumsan quam at, laoreet diam. Nunc euismod hendrerit ante a eleifend. Phasellus nec commodo ligula, a lobortis ipsum. Nullam sit amet libero semper, rutrum erat vel, gravida mauris. Donec rutrum nulla diam, at ultrices enim iaculis euismod.\n" +
            "\n" +
            "Aenean porttitor malesuada orci vitae viverra. Phasellus rhoncus ante eget lacinia efficitur. Duis a elit dapibus ex ullamcorper fringilla. Proin eu hendrerit sem. Vivamus purus ante, maximus elementum suscipit nec, pharetra vitae risus. Vivamus lacinia rutrum nulla, vitae bibendum magna dignissim sed. Proin sed augue sit amet lacus imperdiet cursus nec quis magna. Integer aliquam, libero at volutpat porta, justo odio porttitor erat, et congue libero erat non mauris. Quisque arcu sapien, mattis at metus sit amet, tincidunt luctus enim. Etiam ut mattis tortor, quis placerat diam. Praesent ipsum neque, auctor sed justo eget, feugiat blandit neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec elit risus, mollis lobortis semper iaculis, finibus ac nisi.\n" +
            "\n" +
            "Fusce vel iaculis enim. Fusce tincidunt sed magna eget lobortis. Sed pretium nibh pharetra, volutpat sem suscipit, malesuada orci. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc et nunc fermentum, pretium justo in, ultricies purus. Nulla facilisi. Vivamus varius, ipsum in gravida pharetra, lectus augue vehicula sapien, in vulputate mi risus ac turpis. Cras id eros interdum, lacinia ante vitae, elementum arcu. Morbi molestie ante vitae elit semper suscipit. Sed vel quam a elit placerat ornare. Aliquam erat volutpat. Nunc eu metus neque.\n" +
            "\n" +
            "Duis luctus libero a magna pharetra auctor. Cras mattis scelerisque luctus. Sed pharetra aliquet diam ut sollicitudin. Quisque scelerisque malesuada mi a consectetur. Donec in mollis eros. Aenean sed bibendum sem. Aliquam eget mi sed risus efficitur volutpat vitae sed lorem. Sed ut facilisis nulla. Nulla facilisi. Morbi ultricies mattis sem, vel molestie erat condimentum ac. Cras finibus libero quam, ac ullamcorper orci cursus vehicula.\n" +
            "\n" +
            "Praesent maximus mi id felis egestas, et tristique nisl feugiat. Pellentesque scelerisque faucibus est a molestie. Nunc vehicula erat eget erat semper, nec rhoncus dolor pharetra. Nam vehicula ex id libero cursus, quis molestie velit iaculis. Donec imperdiet, velit a blandit vulputate, urna diam semper neque, sit amet auctor orci enim sit amet est. Nunc metus lacus, venenatis sit amet suscipit ut, ullamcorper sollicitudin nisi. Nullam eleifend neque ipsum, quis feugiat ex finibus in. Duis nisi urna, bibendum sit amet lectus id, accumsan convallis augue. Pellentesque eu elit at nunc faucibus pulvinar at quis odio. Praesent a pretium arcu. Fusce dui est, finibus sed tincidunt vel, sollicitudin vitae ex. Curabitur vitae sollicitudin metus. Pellentesque sit amet lorem porttitor urna pretium consequat sed sit amet ipsum. Fusce eu ullamcorper sem. Pellentesque sem tortor, blandit vitae felis non, finibus mattis dui. Praesent ac magna mauris.\n" +
            "\n" +
            "Integer facilisis turpis eget risus vestibulum, consectetur tempus massa porttitor. Vestibulum congue lacinia varius. Cras facilisis at orci eget finibus. Aliquam non ultricies arcu. Pellentesque egestas metus ut odio dictum, sit amet semper lorem convallis. Curabitur aliquam lectus non semper tempor. Nulla mattis laoreet nibh, vel sollicitudin justo cursus a. Suspendisse faucibus ipsum vel ipsum pretium, ut fermentum tellus vehicula. Sed iaculis ex et feugiat mollis. Phasellus at ornare ipsum, non porta ex. Donec rhoncus posuere enim. Curabitur nunc nisl, sodales in dui sed, rutrum lobortis sapien. Vivamus eget nisl pellentesque risus aliquam congue eget et orci. Vivamus a velit ut nibh consequat sollicitudin sit amet et turpis. Duis scelerisque posuere purus, ut malesuada diam. Mauris viverra eleifend dapibus.\n" +
            "\n" +
            "Integer tempor lorem id convallis hendrerit. Quisque aliquam cursus arcu. Suspendisse ac dolor cursus, imperdiet nunc at, tincidunt lorem. Suspendisse a dictum neque. Nam eleifend erat eu faucibus cursus. Sed libero mi, ultrices et diam nec, viverra venenatis tellus. Nullam ac justo non ligula auctor fringilla. Sed sit amet mi a magna finibus vestibulum eu ut purus. Sed et dapibus nisi. Maecenas ultrices mattis vestibulum. Aenean eu tortor vel augue cursus facilisis vel non metus.\n" +
            "\n" +
            "Morbi scelerisque pharetra blandit. Curabitur aliquet blandit leo ut tristique. In convallis dignissim dolor, at vehicula nisl viverra ac. Mauris iaculis ornare urna, vitae efficitur lacus euismod id. Mauris libero mauris, porttitor vitae consequat et, blandit eget lacus. Aenean id nulla ac elit semper ornare vel at arcu. Etiam nec dui dui. Pellentesque auctor, tellus non fringilla efficitur, turpis massa blandit enim, id accumsan dolor velit viverra sem. Nunc eget ornare nunc. Duis maximus aliquam mauris eu accumsan. In hac habitasse platea dictumst.\n" +
            "\n" +
            "Mauris ullamcorper eros a dui rhoncus, in iaculis odio aliquet. Vivamus felis elit, hendrerit id pharetra ac, porta eget libero. Sed semper pulvinar eros, vel placerat nisl vehicula eu. Vestibulum laoreet at lorem ut viverra. Phasellus ut porta dui. Maecenas eu nisi fringilla, consectetur sapien sit amet, malesuada nisl. Aenean auctor, massa vitae feugiat pellentesque, libero lorem venenatis tellus, eget blandit tellus ex efficitur erat.\n" +
            "\n" +
            "Proin fermentum id eros ut ultrices. Nullam faucibus consequat tortor, nec dignissim odio lobortis ut. Integer finibus massa a tempor porttitor. Cras massa nunc, eleifend sit amet urna porttitor, elementum lacinia enim. Nulla facilisi. Cras neque lectus, mollis congue lacus eu, cursus convallis elit. Phasellus sit amet lectus eu enim dignissim vehicula sed sed sapien. Maecenas ut vehicula lectus, ut euismod eros. Aenean at nibh vitae urna placerat scelerisque. Morbi aliquam accumsan odio, sed ultricies sem hendrerit venenatis.\n" +
            "\n" +
            "Morbi euismod enim sed orci viverra facilisis. Aenean eleifend quam id dui iaculis, eu consequat massa euismod. Vivamus id sapien at nibh commodo consectetur vitae non libero. Sed rhoncus fringilla purus, eget luctus mi eleifend vel. Donec libero nunc, posuere at leo ac, maximus pretium est. Nam tincidunt rhoncus eros, nec vulputate massa interdum vel. Mauris a enim diam. Praesent pharetra justo nec efficitur viverra. Proin varius tristique mi, vel lacinia nisl fermentum ac. Nullam mattis in sem eu tristique. Mauris et auctor nisl. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin semper nibh eu ligula faucibus pellentesque. Morbi imperdiet turpis magna, et accumsan lectus mollis eget. Quisque vestibulum sit amet odio a sollicitudin. Nunc hendrerit pellentesque nisi.\n" +
            "\n" +
            "Nulla eget malesuada nulla, sit amet pulvinar enim. Curabitur eleifend scelerisque ullamcorper. Curabitur molestie ornare ligula non venenatis. Aenean cursus metus mauris, eu vulputate nibh porttitor non. Donec finibus ut ipsum eget feugiat. Integer blandit sollicitudin tellus, id fringilla nunc commodo quis. Pellentesque vestibulum lacus urna. Vestibulum vulputate lectus fermentum ullamcorper ultricies. In hac habitasse platea dictumst. Donec ac erat mauris. Aenean sed augue non erat sodales facilisis at vel nisi.\n" +
            "\n" +
            "Sed volutpat vitae lectus ac posuere. Integer cursus pulvinar lorem quis fringilla. Sed vitae elit malesuada magna viverra dictum eget nec elit. Phasellus nec metus eu eros congue volutpat. Curabitur sed posuere lorem, eget pharetra felis. Vestibulum vehicula sollicitudin orci vitae dignissim. Suspendisse sed massa pretium, congue est nec, auctor neque. Phasellus sit amet suscipit dui. Quisque pellentesque nulla ut sem finibus elementum. Nullam vestibulum elit urna, sit amet volutpat ante iaculis sed. Nulla pretium leo quis massa mattis ultrices. Donec id ipsum sit amet orci eleifend varius. Integer sit amet nulla ac lorem hendrerit fermentum in sit amet purus.\n" +
            "\n" +
            "Mauris vitae augue tellus. Aliquam quis convallis lectus. Nam vitae leo sit amet neque vestibulum ultrices sed tristique mi. Nam eu tincidunt arcu. Duis eget tempus metus, tincidunt gravida nisl. Mauris at lectus ac orci auctor vehicula non eget nulla. Praesent egestas vitae quam vel vestibulum. Pellentesque sed condimentum massa. Sed sollicitudin dolor vitae elit rutrum, semper mattis arcu eleifend. Vivamus sodales fringilla fringilla. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Etiam placerat dictum tempus. Sed placerat, nulla vel ultricies suscipit, neque massa tincidunt velit, nec scelerisque orci ex sit amet lorem.\n" +
            "\n" +
            "Integer vel mattis lacus. Integer at blandit quam, tristique aliquam mauris. Pellentesque commodo maximus congue. Suspendisse potenti. Aenean fermentum ex sit amet sapien malesuada, quis pharetra ipsum efficitur. Maecenas posuere nec orci at dignissim. Cras in est odio. Nulla convallis lobortis metus, at rhoncus purus vestibulum ac. Nunc convallis vestibulum iaculis. Donec sollicitudin est a fermentum imperdiet. Nam at rutrum turpis, sit amet placerat nulla. Nulla facilisi.\n" +
            "\n" +
            "Nunc condimentum neque id nisl fringilla, maximus feugiat ipsum laoreet. Mauris a malesuada turpis. Sed egestas egestas erat ultrices eleifend. Cras scelerisque enim nec suscipit ultricies. Sed et finibus odio. Donec ullamcorper fermentum turpis, in lacinia metus. Duis at sagittis magna, nec fermentum justo. Quisque vehicula erat dolor, ac volutpat metus mattis sit amet. Aliquam rutrum lacus nec sapien auctor pharetra. In rhoncus consequat sagittis. Quisque ligula odio, pretium commodo sapien id, consectetur ultricies odio. Praesent posuere bibendum magna in feugiat. Aenean scelerisque ut turpis id ultricies. Sed quis nisl aliquet, gravida diam at, cursus libero.\n" +
            "\n" +
            "Sed non turpis ut velit venenatis dignissim. Sed mattis massa et tincidunt efficitur. Donec auctor, felis nec ornare accumsan, enim lacus imperdiet arcu, sit amet lacinia nibh lorem a odio. Morbi eu ligula elit. Suspendisse a purus sollicitudin, aliquam turpis vitae, suscipit lacus. Suspendisse potenti. Nullam interdum nisl non mi dapibus, nec euismod sapien elementum. Curabitur faucibus nunc a risus cursus ornare. Interdum et malesuada fames ac ante ipsum primis in faucibus.\n" +
            "\n" +
            "Vivamus viverra tellus non arcu viverra dictum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum sit amet venenatis urna. Aliquam aliquet, mi id placerat cursus, tellus leo efficitur mauris, et euismod purus arcu quis nisl. Aliquam convallis, justo in scelerisque interdum, lorem massa auctor neque, ut lacinia massa lacus a massa. Quisque nibh nisi, efficitur at finibus eget, dictum dignissim lorem. Donec volutpat suscipit diam sed pellentesque. Duis tellus leo, sagittis a sollicitudin porttitor, suscipit nec mauris. Donec malesuada dolor ac ex aliquet, vitae ullamcorper leo luctus. Cras accumsan scelerisque tincidunt. Duis efficitur mauris a dui suscipit, eget sollicitudin ligula ultrices. Duis erat tellus, hendrerit ut pharetra a, tempor in odio. Proin ex diam, bibendum eget suscipit at, porta vel diam. Vivamus imperdiet urna nibh, nec suscipit sem dapibus vitae.\n" +
            "\n" +
            "Donec quis urna in orci tincidunt molestie. Aenean a dictum leo, ultrices luctus velit. Praesent tristique, ex ac porta ullamcorper, turpis ligula venenatis nulla, vel convallis purus mauris quis dolor. Vestibulum sollicitudin massa imperdiet ipsum interdum vestibulum. Aliquam enim arcu, ultricies non turpis a, suscipit sollicitudin nunc. Aenean a porta quam. Suspendisse potenti. Quisque in lectus eget ligula pharetra scelerisque id vitae tellus.\n" +
            "\n" +
            "Phasellus consequat luctus viverra. Curabitur hendrerit ante dapibus justo sagittis tristique. Nullam vehicula aliquam vestibulum. Morbi rhoncus mauris sem, ac ultrices mauris egestas sed. Etiam a laoreet mauris, sed suscipit turpis. In vel libero ultrices, convallis ipsum laoreet, condimentum nisl. Ut iaculis nunc vel nisl aliquet lacinia.\n" +
            "\n" +
            "Donec consequat finibus risus, vel fringilla sem accumsan eget. Vivamus vel felis enim. Sed eget dapibus mi, vitae faucibus sapien. Sed viverra ullamcorper lobortis. Cras at elit vel ipsum vestibulum auctor. Maecenas efficitur augue eu ultricies blandit. Sed in volutpat orci, eu facilisis lacus. Pellentesque ligula tellus, efficitur quis orci non, eleifend cursus mauris. Suspendisse augue dui, vehicula id ante porta, tempus varius enim. Fusce ac purus sollicitudin, bibendum est eu, commodo urna. Vivamus dictum non justo vitae convallis.\n" +
            "\n" +
            "Aliquam convallis elit eget accumsan viverra. In nulla urna, porta sed scelerisque ullamcorper, congue quis magna. Duis enim purus, euismod eu lacinia sit amet, pharetra vel ligula. Nam non eros congue, pulvinar libero a, tempor tellus. Vestibulum viverra imperdiet bibendum. Donec cursus molestie laoreet. Nulla fermentum dictum enim, at dapibus quam.\n" +
            "\n" +
            "Integer convallis libero ligula, at elementum odio tincidunt ut. Fusce lacinia mi ligula, at mattis eros semper a. Donec id enim vel elit tincidunt bibendum vitae non velit. Curabitur a risus felis. Donec sollicitudin, risus quis porttitor malesuada, orci quam consectetur sem, et aliquet lorem orci non orci. Maecenas in arcu tempus, facilisis dui id, viverra tellus. In varius laoreet ligula et tincidunt. Mauris non dui ut mauris tempus ullamcorper non eleifend neque. Suspendisse viverra faucibus semper. Quisque in elit finibus, rutrum libero sit amet, tempor nunc.\n" +
            "\n" +
            "Etiam euismod mauris id orci congue hendrerit. Cras ut massa augue. Mauris id odio viverra, gravida ex rhoncus, laoreet tellus. Curabitur diam tortor, sagittis vel dictum sit amet, lacinia a tortor. Pellentesque cursus ex sed nulla imperdiet porta. Curabitur semper eleifend metus id lacinia. Etiam at iaculis ligula, id consequat neque. Vestibulum tristique, purus quis hendrerit ultrices, ligula lorem gravida ex, in fringilla dui dui et ex. Quisque non dolor magna. Nulla lobortis ut nisi et mollis. Ut tempus varius nibh, aliquam vehicula eros convallis id. Cras vel fermentum eros, in efficitur magna.\n" +
            "\n" +
            "Nunc eget tellus cursus, facilisis dolor eget, ornare nibh. Curabitur dapibus ultrices magna, quis consectetur neque mattis ac. Phasellus auctor rhoncus augue, quis ultricies nunc commodo at. Mauris commodo commodo magna vitae condimentum. Fusce eu viverra tortor. Praesent metus sapien, fermentum a vehicula cursus, feugiat in odio. Nulla eget libero convallis, scelerisque diam eu, accumsan odio. Duis molestie turpis vel tristique vestibulum.\n" +
            "\n" +
            "Nam eu sem orci. Quisque egestas orci commodo ex aliquet faucibus. Phasellus a lacinia ex. Donec ut venenatis tellus, sit amet bibendum enim. Praesent sapien erat, blandit at vestibulum non, ultrices sagittis leo. Fusce eleifend placerat est, vel elementum velit lacinia accumsan. Sed ante nulla, rhoncus sit amet porta et, imperdiet a lorem.\n" +
            "\n" +
            "Aliquam nec massa leo. Nam vestibulum, velit nec pharetra tristique, elit odio hendrerit erat, quis imperdiet lorem elit ac justo. Sed lorem ligula, ornare quis fermentum vel, semper id lorem. Sed lacus eros, dapibus ut ipsum et, vehicula sollicitudin erat. Curabitur sollicitudin ipsum dolor. Vivamus tristique felis a purus scelerisque, quis suscipit quam congue. Phasellus molestie pretium semper. Etiam libero libero, pretium sed leo nec, lacinia aliquam libero. Etiam a augue sed dui imperdiet varius eget eget elit. Nam ac lorem et nulla feugiat posuere vitae tempor nulla. Fusce tristique dui dolor, non hendrerit augue euismod quis.\n" +
            "\n" +
            "Aliquam at mauris dui. Pellentesque eu tempus velit. Cras nibh mi, suscipit ut facilisis sodales, fringilla in eros. Vivamus ac massa ut purus hendrerit scelerisque a sit amet quam. Sed quis mauris vel lorem venenatis porta nec non massa. Etiam id velit dignissim, dignissim lacus id, consectetur ex. Sed id odio ut erat iaculis fermentum. Etiam a mi eu ante tempor tincidunt congue eleifend ante. Ut suscipit quis nisi non cursus. Mauris blandit iaculis lectus, sit amet dictum sapien accumsan a. Quisque pharetra gravida ultrices. Duis interdum pulvinar risus, et mattis elit imperdiet sed. Suspendisse sed massa nisl. Pellentesque augue diam, lobortis eu ante in, volutpat pellentesque libero. Vestibulum ipsum sem, ultricies nec felis nec, imperdiet facilisis erat. Integer sollicitudin consequat eros, et luctus risus interdum at.\n" +
            "\n" +
            "Vivamus nec sapien vel tortor dapibus fermentum. Proin euismod enim eget velit hendrerit consequat. Morbi nec urna facilisis, bibendum sapien non, consequat metus. Ut nec sapien tempor velit luctus mollis eu nec sapien. Quisque sollicitudin lacus eget feugiat porttitor. Praesent suscipit nulla non venenatis sollicitudin. Donec molestie turpis id lorem cursus fermentum. Maecenas et ex sed leo viverra posuere. Nam vitae mi iaculis lorem sollicitudin semper. Morbi luctus lacus vitae sem accumsan, quis gravida diam faucibus. Proin tristique vitae purus ac semper. Praesent placerat id massa a vehicula. Aliquam erat volutpat. Sed at justo vulputate, scelerisque risus et, sodales tellus.\n" +
            "\n" +
            "Donec eget tincidunt lectus. Sed rhoncus velit vitae ultrices sodales. Integer tempor dictum ex, quis laoreet neque porta quis. Nam vel dui quam. Sed rutrum neque eu efficitur tristique. Morbi vel diam vitae diam interdum hendrerit id sit amet neque. Quisque at laoreet libero, ornare efficitur velit. Nulla facilisi. Duis vel nisl at ligula sollicitudin porttitor ullamcorper sed massa. Nulla sollicitudin ligula elementum, suscipit velit ut, egestas magna. Phasellus vel interdum lectus, quis dapibus nisi. Phasellus vel ipsum eu est molestie finibus eget nec nisi. Nulla facilisi. Nunc feugiat fermentum ipsum, a tempor sapien bibendum vitae.\n" +
            "\n" +
            "Nam imperdiet purus vel ultricies consequat. Proin odio sem, porttitor id finibus a, aliquet eget nibh. Quisque at sapien et dolor consectetur hendrerit. Aenean quis ultrices urna, ut lacinia tellus. Duis efficitur neque at massa iaculis, sed finibus ligula tempus. Morbi at lobortis dolor. In erat dui, convallis at luctus laoreet, ullamcorper eu est. Aenean maximus augue at laoreet semper. Aenean eu nulla at urna placerat efficitur et id lacus. Sed vel enim at erat luctus venenatis. Nullam hendrerit aliquam dolor, eget convallis ligula tincidunt ut.\n" +
            "\n" +
            "Cras rutrum at lorem eu tempus. Etiam eget urna in ligula feugiat sagittis at vitae leo. Integer gravida dui vitae ipsum condimentum consectetur. Curabitur ac volutpat dolor. Curabitur interdum leo nunc, a cursus libero vulputate at. Duis at aliquet diam, a dictum nisl. Suspendisse est risus, facilisis porta neque et, ultricies volutpat leo. Sed pulvinar velit id eleifend fringilla. Etiam ut sagittis ligula. In hac habitasse platea dictumst. Cras eget ornare turpis, non sodales urna. Mauris dictum nulla dui, at commodo tellus commodo ut. Duis in consectetur justo. Sed eu hendrerit nulla. Duis sodales pulvinar odio, egestas tristique massa commodo ut. Pellentesque nulla nisi, ultrices at felis sed, dignissim mollis augue.\n" +
            "\n" +
            "Nam eu orci dui. Donec feugiat nisi maximus, rhoncus orci id, accumsan diam. Maecenas lobortis metus et pharetra luctus. Nunc laoreet magna vestibulum massa vulputate tempus ut sed augue. Nam bibendum tortor auctor condimentum convallis. In hac habitasse platea dictumst. Duis quam mi, convallis vehicula elementum quis, efficitur ac felis. Proin in eros nisi. Duis pulvinar lorem id tellus euismod lobortis at id enim. Curabitur tincidunt dolor in dignissim ultricies. Suspendisse in ante sed erat eleifend viverra quis laoreet turpis. Suspendisse eget euismod ipsum, vel venenatis justo. Cras at dui vitae turpis pharetra elementum non a lacus.\n" +
            "\n" +
            "Duis dapibus diam id lectus tempus tempor. Duis rhoncus, sapien sed commodo blandit, nunc velit pretium est, ut rutrum purus nunc tempor lacus. Morbi suscipit pharetra convallis. Vivamus diam quam, interdum et nisl sed, mattis efficitur nisi. Nunc felis ex, elementum id fermentum consectetur, sagittis a ipsum. Cras consequat sodales est, at vehicula metus luctus vitae. Vivamus posuere nisi vel lacinia rhoncus. Phasellus libero sapien, tempor sit amet metus ut, venenatis maximus nulla. Nunc sit amet vehicula quam, at commodo lacus. Phasellus commodo erat vel odio porta tristique. Praesent eget nisi eget augue hendrerit vulputate eu a libero.\n" +
            "\n" +
            "Duis vehicula mi in egestas finibus. Maecenas iaculis rutrum mauris, eu facilisis quam tempor aliquam. Nunc hendrerit ipsum ut purus venenatis, non ultricies dui aliquet. Curabitur maximus pulvinar ultricies. Ut volutpat metus et pellentesque gravida. Etiam eu nibh fermentum, ullamcorper ex ut, convallis justo. Duis vel enim ut purus pellentesque consectetur non nec augue. Pellentesque nec luctus nisi, nec lobortis odio. Fusce finibus tincidunt ultricies.\n" +
            "\n" +
            "Nunc consectetur tellus nisi, eu dignissim lacus tristique et. Nam eget odio eu sem ullamcorper ultrices a eget nunc. Donec malesuada lorem at arcu vulputate, at aliquam libero egestas. Nullam id est ut dolor placerat aliquet at eu urna. Duis pulvinar at justo pharetra laoreet. Pellentesque porta augue at libero luctus mollis. Phasellus posuere quis nibh id egestas. Mauris suscipit iaculis tortor, tempor rutrum ante egestas sollicitudin. Duis ut massa ut mi pellentesque tristique. Nullam efficitur ipsum quam. Nulla neque elit, fringilla vel tincidunt et, vestibulum dapibus turpis.\n" +
            "\n" +
            "Ut elementum, massa sed ornare sollicitudin, libero dui lacinia nulla, at efficitur metus risus ut nisi. Mauris vestibulum ex ut aliquet commodo. Integer porttitor, ligula sed egestas pulvinar, risus lacus venenatis nisi, eu aliquam libero risus at lectus. Aenean ultricies tortor dolor, vel pellentesque neque aliquet quis. Nunc pretium tortor eu maximus feugiat. Proin suscipit est eget enim ultricies, in sollicitudin tellus posuere. Fusce porttitor pharetra tempor. In malesuada justo vitae euismod molestie. In tortor massa, tempus sit amet sem vitae, consequat bibendum sem. Sed mollis porttitor tortor euismod volutpat. Praesent et tellus quis nibh aliquet pharetra. Ut eros odio, lacinia eu felis sed, pretium vehicula augue. Maecenas consequat, sem et convallis pretium, est quam suscipit odio, nec scelerisque lorem nunc non odio. Morbi sit amet varius tortor. Sed non ante nisi.\n" +
            "\n" +
            "Cras tortor urna, commodo nec eleifend at, tempor vitae ipsum. Sed blandit magna nec vulputate pulvinar. Pellentesque ullamcorper ex in felis feugiat, fringilla condimentum enim fringilla. Ut tincidunt rhoncus pharetra. Integer eu facilisis odio, vel iaculis felis. Aliquam erat volutpat. Nulla vitae sapien nec metus consequat tempus. Sed non pretium risus. Aenean hendrerit lacinia nibh a dignissim. Nam blandit suscipit efficitur. Etiam eget ligula sit amet ipsum imperdiet pellentesque vitae a est. Curabitur commodo gravida odio vel convallis. Duis congue sapien ac ante tristique, condimentum lacinia orci accumsan. Nullam enim leo, fringilla eget tincidunt vitae, tincidunt in sapien. Donec ut aliquet dui.\n" +
            "\n" +
            "Donec nulla velit, finibus vitae vulputate nec, pulvinar id dui. Cras consectetur tincidunt ante, molestie aliquam mauris accumsan sed. Maecenas sagittis interdum ipsum vitae varius. Maecenas imperdiet vel leo vitae volutpat. Nam vulputate vulputate est ac ultrices. Mauris rutrum est sed enim gravida, sed posuere urna sollicitudin. Nullam ante eros, fermentum vitae pellentesque vel, faucibus eu ipsum. Praesent varius scelerisque turpis id ornare. Sed lobortis tempor turpis in dignissim. In hac habitasse platea dictumst. Morbi id lectus dui. Donec eu neque malesuada, hendrerit massa et, rhoncus urna.\n" +
            "\n" +
            "Donec orci risus, scelerisque sit amet augue at, pharetra finibus augue. Mauris ullamcorper aliquam massa, id sollicitudin ex rhoncus eu. In leo metus, pretium quis nisi ornare, faucibus blandit nulla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed sed porta felis, sed mollis orci. Phasellus eleifend nunc a neque finibus, eget dapibus sem scelerisque. Donec nunc orci, volutpat ac rutrum a, cursus ac elit. Nulla facilisi. Curabitur semper urna a ex condimentum, id ullamcorper est egestas. Duis varius mi a vestibulum laoreet. Sed vitae elementum urna, vitae luctus quam. Vivamus molestie neque et faucibus bibendum. Nullam semper, purus id dictum volutpat, erat elit dictum mauris, vitae fermentum nisl tellus ut velit. Nulla lorem nisl, sollicitudin sed placerat in, consequat in justo. Curabitur et felis fringilla, sodales mi in, feugiat lectus.\n" +
            "\n" +
            "Fusce rhoncus felis auctor felis porta, finibus pretium purus suscipit. Maecenas pretium dapibus dolor nec mollis. Donec venenatis pretium iaculis. Maecenas at tristique massa. Nunc ut sem vitae sapien pulvinar feugiat. Quisque sit amet tempor orci. Proin vehicula, ex eget tincidunt accumsan, justo erat fringilla diam, ac vulputate risus leo sit amet nunc. Duis vel magna et dolor sodales convallis. Mauris rutrum iaculis ante in molestie. Integer non velit vulputate nunc posuere varius. Fusce dictum lectus ut ullamcorper elementum. Ut dignissim sapien nisl, ac porttitor nunc sodales non. Quisque et odio quis augue feugiat placerat venenatis eget eros. Nunc dapibus finibus erat vulputate malesuada. Nunc semper leo vitae efficitur ornare. In hac habitasse platea dictumst.\n" +
            "\n" +
            "Nam convallis non odio non venenatis. Mauris eu enim euismod dolor elementum placerat. Morbi pretium nulla orci, ut faucibus nisl malesuada eu. Quisque ut nulla eget purus imperdiet ornare. Nunc ac finibus ex. Phasellus sagittis quam sed mattis ornare. Praesent quis lacus aliquet, semper ipsum non, pulvinar quam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed semper elit sed dui fermentum fringilla. Aenean finibus velit lectus, ut euismod metus sollicitudin vitae. Donec a vulputate enim, sed ultricies ante. Maecenas efficitur est sed augue dapibus, ut dictum mauris aliquet.\n" +
            "\n" +
            "Curabitur sed imperdiet nisl. Curabitur pulvinar est quis metus rutrum lacinia. Suspendisse tincidunt tempor arcu, eu consectetur tortor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aenean bibendum posuere eros, quis feugiat nibh vestibulum gravida. Pellentesque id justo nec odio laoreet malesuada sit amet sed dolor. Vestibulum condimentum libero id elit ullamcorper, non congue nulla porttitor. Nunc sapien.";

    @Test
    void lz4CompressAndDecompress() {
        LZ4Factory factory = LZ4Factory.fastestInstance();

        byte[] data = this.data.getBytes(StandardCharsets.UTF_8);
        final int decompressedLength = data.length;
        System.out.println("data length = " + decompressedLength);

        // compress data
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
        System.out.println("compressed length = " + compressedLength);

        // decompress data
        // - method 1: when the decompressed length is known
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[decompressedLength];
        int compressedLength2 = decompressor.decompress(compressed, 0, restored, 0, decompressedLength);
        System.out.println("decompress length = " + restored.length);
    }

    @Test
    void contextLoads() {

        System.out.println("Data");
        System.out.println("-----------------------------------------------");

//        System.out.println(data);
        System.out.println(data.length());
        System.out.println();

        System.out.println("Compressed result");
        System.out.println("-----------------------------------------------");
        String qrCodeCompressedData = compressionService.lz4Compress(data);
//        System.out.println(qrCodeCompressedData);
        System.out.println(qrCodeCompressedData.length());
        System.out.println();

        System.out.println("Decompress result");
        System.out.println("-----------------------------------------------");
        String d = compressionService.lz4Uncompress(qrCodeCompressedData);
//        System.out.println(d);
        System.out.println(d.length());
        System.out.println();

    }

    @Test
    void qrCodeImageDetection() throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream("images/photo_2022-01-10_17-13-38.jpg"));
        ArrayList<String> codes = qrCodeService.detectQRCodes(is);
        System.out.println("[QR Codes Detected] : " + codes.size());
        codes.forEach(System.out::println);
        System.out.println(utilsService.combineQRCodes(codes));
    }

    @Test
    void test() {
        System.out.println(Colors.BLACK.getArgb());
    }


    @Test
    void openCV() {
        System.load("C:\\openCV\\build\\java\\x64\\opencv_java455.dll");
        QRCodeDetector a = new QRCodeDetector();
        java.util.List<String> s = new ArrayList<>();
        Mat image = Imgcodecs.imread("images/qrCode.png");
        Mat mGray = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        boolean res = a.detectAndDecodeMulti(mGray, s);
        System.out.println(res);
        System.out.println(s);
        s.forEach(System.out::println);
    }

    @Test
    void videoOpenCV() throws IOException {
        System.load("C:\\openCV\\build\\java\\x64\\opencv_java455.dll");
        String[] videos = new String[]{
                "20220110_164912.MP4",
                "20220110_170334.MP4",
                "20220110_170458.MP4",
                "video_2022-01-10_17-09-10.mp4",
                "video_2022-01-10_17-09-14.mp4",
                "video_2022-01-10_17-13-34.mp4",
                "video_2022-01-10_17-18-35.mp4",
                "video_2022-01-10_17-18-38.mp4"
        };
        VideoCapture video = new VideoCapture("videos/" + videos[0]);
        int count = 0;
        while (video.grab()) {
            System.out.println("[Frame] : " + ++count);
            Mat image = new Mat();
            video.retrieve(image);
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            if (byteArray.length > 0) {
                ArrayList<String> codes = qrCodeService.detectQRCodes(new ByteArrayInputStream(byteArray));
                System.out.println("[QR Codes Detected] : " + codes.size());
                codes.forEach(System.out::println);
                try {
                    String combinedQRCode = compressionService.lz4Uncompress(utilsService.combineQRCodes(codes));
                    System.out.println(combinedQRCode.substring(0, 100));
                } catch (Exception e) {
                    System.out.println("Decompress Failed");
                }
            }
        }

    }

    @Test
    void videoFFMPEG() throws IOException {
        String[] videos = new String[]{
                "video_2022-01-17_17-05-51.mp4"
        };
        InputStream is = new BufferedInputStream(new FileInputStream("videos/" + videos[0]));
        utilsService.iterateVideoFrames(is, byteArray -> {
            if (byteArray.length > 0) {
                ArrayList<String> codes = null;
                try {
                    codes = qrCodeService.detectQRCodes(new ByteArrayInputStream(byteArray));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("[QR Codes Detected] : " + codes.size());
                codes.forEach(System.out::println);
                String combinedQRCode = compressionService.lz4Uncompress(utilsService.combineQRCodes(codes));
                if (combinedQRCode.length() == 0) {
                    System.out.println("Decompress Failed");
                }
                else {
                    System.out.println(combinedQRCode.substring(0, 100));
                }
            }
            System.out.println();
        });
    }
}
