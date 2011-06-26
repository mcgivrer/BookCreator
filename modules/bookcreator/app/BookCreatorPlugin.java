import play.*;
import play.mvc.Http.*;
import play.mvc.*;
import play.libs.*;
import play.vfs.*;

import java.io.*;

public class BookCreatorPlugin extends PlayPlugin {

    @Override
    public boolean rawInvocation(Request request, Response response) throws Exception {
        if ("/@book".equals(request.path) || "/@book/".equals(request.path)) {
            response.status = 302;
            response.setHeader("Location", "/@book/home.html");
            return true;
        }
        if (request.path.startsWith("/@book/")) {
            if(request.path.matches("/@book/-[a-z]+/.*")) {
                String module = request.path.substring(request.path.indexOf("-")+1);
                module = module.substring(0, module.indexOf("/"));
                VirtualFile f = Play.modules.get(module).child("book/"+request.path.substring(8+module.length()));
                if(f.exists()) {
                    response.contentType = MimeTypes.getMimeType(f.getName());
                    response.out.write(f.content());
                }
                return true;
            }
            File f = new File(Play.applicationPath, "/pages/chapters/"+request.path.substring(6));
            if (f.exists()) {
                response.contentType = MimeTypes.getMimeType(f.getName());
                response.out.write(IO.readContent(f));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRoutesLoaded() {
        Router.prependRoute("GET", "/@book/?", "BookCreator.index");
        Router.prependRoute("GET", "/@book/{id}", "BookCreator.page");
        Router.prependRoute("GET", "/@book/images/{name}", "BookCreator.image");
        Router.prependRoute("GET", "/@book/files/{name}", "BookCreator.file");
        Router.prependRoute("GET", "/@book/references/{category}", "BookCreator.reference");
        //Router.prependRoute("GET", "/@book/modules/{module}/{id}", "BookCreator.page");
        //Router.prependRoute("GET", "/@book/modules/{module}/images/{name}", "BookCreator.image");
        //Router.prependRoute("GET", "/@book/modules/{module}/files/{name}", "BookCreator.file");
    }

}
