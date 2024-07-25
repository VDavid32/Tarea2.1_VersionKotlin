package Models

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.pm1e2_grupo5.R

class PersonaAdapterGet(private val context: Context, private val personaList: List<Persona>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return personaList.size
    }

    override fun getItem(position: Int): Any {
        return personaList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_persona, parent, false)
        }

        val imageViewFirma = convertView.findViewById<ImageView>(R.id.imageViewFirma)
        val textViewNombre = convertView.findViewById<TextView>(R.id.textViewNombre)
        val textViewTelefono = convertView.findViewById<TextView>(R.id.textViewTelefono)
        val textViewLatitud = convertView.findViewById<TextView>(R.id.textViewLatitud)
        val textViewLongitud = convertView.findViewById<TextView>(R.id.textViewLongitud)


        //Button buttonDetails = convertView.findViewById(R.id.buttonDetails);
        val persona = getItem(position) as Persona

        textViewNombre.text = "" + persona.id + " " + persona.nombre
        textViewTelefono.text = persona.telefono
        textViewLatitud.text = persona.latitud
        textViewLongitud.text = persona.longitud

        // Decodificar la imagen base64 y cargarla en ImageView
        if (persona.firma != null && !persona.firma.isEmpty()) {
            val decodedString = Base64.decode(persona.firma, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imageViewFirma.setImageBitmap(decodedByte)
        } else {
            // imageViewFirma.setImageResource(R.drawable.default_image); // Imagen por defecto
        }


        /*buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetallesActivity.class);
                intent.putExtra("latitud", Double.parseDouble(persona.getLatitud()));
                intent.putExtra("longitud", Double.parseDouble(persona.getLongitud()));
                context.startActivity(intent);
            }
        });*/
        return convertView
    }
}
