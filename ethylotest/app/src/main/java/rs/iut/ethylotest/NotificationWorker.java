package rs.iut.ethylotest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/** Worker exécuté en arrière-plan pour envoyer la notification "peut conduire". */
public class NotificationWorker extends Worker {

    /** Crée le worker avec le contexte et les paramètres fournis par WorkManager. */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /** Vérifie le taux actuel et envoie la notification si la personne peut conduire. */
    @NonNull
    @Override
    public Result doWork() {
        AlcoolRepository repository = new AlcoolRepository(getApplicationContext());
        double taux = AlcoolCalculateur.calculerTauxActuel(repository.loadTaux(), repository.loadTimestamp());

        Personne personne = repository.loadPersonne();
        double seuil = personne != null
                ? AlcoolCalculateur.getSeuil(personne)
                : Constantes.SEUIL_NORMAL;

        if (taux <= seuil) {
            envoyerNotification();
        }

        return Result.success();
    }

    private void envoyerNotification() {
        Context ctx = getApplicationContext();
        NotificationManager manager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    Constantes.NOTIF_CHANNEL_ID,
                    ctx.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, Constantes.NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ctx.getString(R.string.notif_titre))
                .setContentText(ctx.getString(R.string.notif_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(Constantes.NOTIF_ID, builder.build());
    }
}
